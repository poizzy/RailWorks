package poizzy.railworks.render;

import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.render.obj.OBJRender;
import cam72cam.mod.render.opengl.RenderState;
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

    private static final Pattern statePattern = Pattern.compile("STATE_([^_]+)");

    public boolean isStateDependent(String group) {
        return hasFlag(group, "STATE");
    }

    public boolean isLit(String group, TileBlock block) {
        Matcher matcher = statePattern.matcher(group);
        if (matcher.find()) {
            String lightState = matcher.group(1).replace("^", " ");
            return lightState.equals(block.getState()) && hasFlag(group, "FULLBRIGHT");
        }
        return false;
    }

    public void renderBlock(TileBlock block, RenderState state, float partialTicks) {
        state.lighting(true)
                .cull_face(false)
                .rescale_normal(true);
        state.translate(0.5, 0, 0.5);
        state.rotate(block.getAngle(), 0, 1, 0);

        Binder binder = binder().texture(block.getTexture());
        try (OBJRender.Binding bound = binder.bind(state)) {
            Map<String, Matrix4> animatedGroups = new HashMap<>();
            for (String s : groups()) {
                for (BlockAnimation animation : animations) {
                    Matrix4 anim;
                    if ((anim = animation.getMatrix(block, s, partialTicks)) != null) {
                        animatedGroups.put(s, anim);
                    }
                }
            }

            List<String> notAnimated = groups().stream().filter(s -> !animatedGroups.containsKey(s)).collect(Collectors.toList());

            // Draw animated
            for (Map.Entry<String, Matrix4> anim : animatedGroups.entrySet()) {
                String obj = anim.getKey();
                if (isStateDependent(obj) && isLit(obj, block)) {
                    bound.draw(Collections.singletonList(obj), s -> s.lightmap(1, 1).model_view().multiply(anim.getValue()));
                } else {
                    bound.draw(Collections.singletonList(obj), s -> s.model_view().multiply(anim.getValue()));
                }
            }

            // Draw static

            Map<Boolean, List<String>> isLit = notAnimated.stream().collect(Collectors.partitioningBy(s -> isStateDependent(s) && isLit(s, block)));

            bound.draw(isLit.get(true), s -> s.lightmap(1, 1));
            bound.draw(isLit.get(false));
        }
    }
}
