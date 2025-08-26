package poizzy.railworks.tile;

import cam72cam.mod.block.BlockEntityTickable;
import cam72cam.mod.entity.boundingbox.IBoundingBox;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.serialization.StrictTagMapper;
import cam72cam.mod.serialization.TagCompound;
import cam72cam.mod.serialization.TagField;
import poizzy.railworks.RWItems;
import poizzy.railworks.items.ItemSignal;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.registry.DefinitionManager;

public class TileBlock extends BlockEntityTickable {
    @TagField
    private String defId;
    @TagField(value = "texture", mapper = StrictTagMapper.class)
    private String texture = null;

    @Override
    public void update() {

    }

    public TileBlock setup(String defId, String texture) {
        this.defId = defId;
        this.texture = texture;
        return this;
    }

    @Override
    public ItemStack onPick() {
        ItemStack stack = new ItemStack(RWItems.ITEM_SIGNAL, 1);
        ItemSignal.Data data = new ItemSignal.Data(stack);
        data.block = getDefinition();
        data.texture = texture;
        data.write();
        return stack;
    }

    public BlockDefinition getDefinition() {
        return DefinitionManager.getDefinition(defId);
    }

    public String getTexture() {
        return this.texture;
    }

    @Override
    public IBoundingBox getRenderBoundingBox() {
        BlockDefinition def = getDefinition();
        Vec3d min = def.model.minOfGroup(def.model.groups());
        Vec3d max = def.model.maxOfGroup(def.model.groups());
        return IBoundingBox.from(min, max);
    }

    @Override
    public IBoundingBox getBoundingBox() {
        BlockDefinition def = getDefinition();
        Vec3d min = def.model.minOfGroup(def.model.groups()).add(0.5, 0, 0.5);
        Vec3d max = def.model.maxOfGroup(def.model.groups()).add(0.5 , 0, 0.5);
        return IBoundingBox.from(min, max);
    }
}
