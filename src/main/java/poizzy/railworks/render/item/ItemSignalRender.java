package poizzy.railworks.render.item;

import cam72cam.mod.item.ItemStack;
import cam72cam.mod.model.obj.OBJModel;
import cam72cam.mod.render.ItemRender;
import cam72cam.mod.render.StandardModel;
import cam72cam.mod.render.obj.OBJRender;
import cam72cam.mod.render.opengl.RenderState;
import cam72cam.mod.world.World;
import poizzy.railworks.items.ItemSignal;

import java.util.HashMap;
import java.util.Map;

public class ItemSignalRender implements ItemRender.IItemModel {
    public Map<String, OBJModel> cache = new HashMap<>();

    @Override
    public StandardModel getModel(World world, ItemStack stack) {
        ItemSignal.Data data = new ItemSignal.Data(stack);
        return new StandardModel().addCustom((state, partialTicks) -> {
            try {
                OBJModel model = cache.computeIfAbsent(data.block.defId, k -> {
                    try {
                        return new OBJModel(data.block.modelLoc, 0);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
                try (OBJRender.Binding vbo = model.binder().texture(data.texture).bind(state)) {
                    vbo.draw(model.groups());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void applyTransform(ItemStack stack, ItemRender.ItemRenderType type, RenderState ctx) {
        ctx.scale(0.1, 0.1, 0.1);
        ctx.rotate(-90, 0, 1, 0);
    }
}
