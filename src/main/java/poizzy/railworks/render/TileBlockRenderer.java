package poizzy.railworks.render;

import cam72cam.mod.math.Vec3i;
import cam72cam.mod.render.StandardModel;
import poizzy.railworks.tile.TileBlock;

import java.util.HashMap;
import java.util.Map;

public class TileBlockRenderer {
    private static final Map<Vec3i, BlockModel<?, ?>> renderers = new HashMap<>();

    public static StandardModel render(TileBlock te) {
        BlockModel<?, ?> renderer = renderers.computeIfAbsent(te.getPos(), k -> {
            try {
                return new BlockModel<>(te.getDefinition());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return new StandardModel().addCustom((state, partialTicks) -> renderer.renderBlock(te, state, partialTicks));
    }

    public static void removeRenderer(Vec3i pos) {
        renderers.remove(pos);
    }
}
