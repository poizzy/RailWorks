package poizzy.railworks.blocks;

import cam72cam.mod.block.BlockEntity;
import cam72cam.mod.block.BlockTypeEntity;
import poizzy.railworks.tile.TileController;

public class BlockController extends BlockTypeEntity {
    public BlockController(String modID, String name) {
        super(modID, name);
    }

    @Override
    protected BlockEntity constructBlockEntity() {
        return new TileController();
    }
}
