package poizzy.railworks.render;

import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.render.obj.OBJRender;
import cam72cam.mod.render.opengl.RenderState;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.tile.TileBlock;

import java.util.ArrayList;

public class BlockModel<DEFINITION extends BlockDefinition, TILE extends TileBlock> extends OBJModel {
    private final DEFINITION def;

    public BlockModel(DEFINITION def) throws Exception {
        super(def.modelLoc, 0, new ArrayList<>());
        this.def = def;
    }

    protected void effects(TILE tile) {

    }

    protected void removed(TILE tile) {

    }

    public void renderBlock(TileBlock block, RenderState state, float partialTicks) {
        state.lighting(true)
                .cull_face(false)
                .rescale_normal(true);
        state.translate(0.5, 0, 0.5);

        Binder binder = binder().texture(block.getTexture());
        try (OBJRender.Binding bound = binder.bind(state)) {
            bound.draw(groups());
        }
    }
}
