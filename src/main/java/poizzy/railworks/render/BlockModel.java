package poizzy.railworks.render;

import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.render.obj.OBJRender;
import cam72cam.mod.render.opengl.RenderState;
import org.apache.commons.lang3.tuple.Pair;
import poizzy.railworks.animation.BlockAnimation;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.tile.TileBlock;
import util.Matrix4;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class BlockModel<DEFINITION extends BlockDefinition, TILE extends TileBlock> extends OBJModel {
    private final DEFINITION def;
    private final List<BlockAnimation> animations;

    public BlockModel(DEFINITION def) throws Exception {
        super(def.modelLoc, 0, def.signalStates.values().stream().map(v -> v.texture).collect(Collectors.toSet()));
        this.def = def;

        animations = new ArrayList<>();
        for (BlockDefinition.States states : def.signalStates.values()) {
            if (states.animation == null) continue;
            animations.add(new BlockAnimation(states));
        }
    }

    protected void effects(TILE tile) {

    }

    protected void removed(TILE tile) {

    }

    public boolean hasFlag(String object, String flag) {
        String[] sep = object.split("_");
        for (String s : sep) {
            if (s.equals(flag)) {
                return true;
            }
        }
        return false;
    }

    private static final Pattern STATE_INFO = Pattern.compile("(FULLBRIGHT|HIDE)_([^_]+)");

    public Pair<Boolean, Boolean> isHiddenOrLit(String group, TileBlock block) {
        if (group == null || block == null) return Pair.of(false, false);

        final String stateName = block.getState();
        if (stateName == null) return Pair.of(false, false);
        final BlockDefinition.States blockState = block.getDefinition().signalStates.get(stateName);
        boolean hidden = false;
        boolean fullbright = false;

        Matcher matcher = STATE_INFO.matcher(group);
        while (matcher.find()) {
            String kind = matcher.group(1);

            String spec = matcher.group(2).replace('^', ' ');

            boolean matches = matchesAny(spec, blockState);

            if ("HIDE".equals(kind)) {
                hidden = matches;
            } else {
                fullbright = matches;
            }
        }

        return Pair.of(hidden, fullbright);
    }

    private static boolean matchesAny(String spec, BlockDefinition.States state) {
        if (state.controlGroups.isEmpty()) return spec.contains("*");
        for (String token : spec.split("\\|")) {
            if (token.equals("*")) return true;
            boolean invert = !token.isEmpty() && token.charAt(0) == '~';
            String value = invert ? token.substring(1) : token;
            boolean eq = state.controlGroups.contains(value);
            if (invert != eq) return true;
        }
        return false;
    }

    public final List<String> singletonList = new ArrayList<>(1);

    public void renderBlock(TileBlock block, RenderState state, float partialTicks) {
        state.lighting(true)
                .cull_face(false)
                .rescale_normal(true);
        state.translate(0.5, 0, 0.5);
        state.rotate(block.getAngle(), 0, 1, 0);

        Binder binder = binder().texture(block.getTexture());
        try (OBJRender.Binding bound = binder.bind(state)) {
            for (String group : groups()) {
                Pair<Boolean, Boolean> hiddenOrLit = isHiddenOrLit(group, block);
                if (hiddenOrLit.getLeft()) continue;

                boolean lit = hiddenOrLit.getRight();

                singletonList.add(group);

                Matrix4 matrix = null;
                for (BlockAnimation animation : animations) {
                    Matrix4 anim;
                    if ((anim = animation.getMatrix(block, group, partialTicks)) != null) {
                        matrix = anim;
                        break;
                    }
                }

                Matrix4 finalMatrix = matrix;
                bound.draw(singletonList, s -> {
                    s.lightmap(lit ? 1 : 0, 1);
                    if (finalMatrix != null) {
                        s.model_view().multiply(finalMatrix);
                    }
                });

                singletonList.clear();
            }
        }
    }
}
