package poizzy.railworks.blocks;

import cam72cam.mod.block.BlockEntity;
import cam72cam.mod.block.BlockTypeEntity;
import poizzy.railworks.items.ItemSignal;
import poizzy.railworks.tile.TileSignal;

public class BlockSignal extends BlockTypeEntity {
    private ItemSignal.Data data;

    public BlockSignal(String modID, String name) {
        super(modID, name);
    }

    public BlockSignal setData(ItemSignal.Data data) {
        this.data = data;
        return this;
    }

    @Override
    protected BlockEntity constructBlockEntity() {
        return new TileSignal();
    }
}
