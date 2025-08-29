package poizzy.railworks;

import poizzy.railworks.blocks.BlockController;
import poizzy.railworks.blocks.BlockSignal;

public class RWBlocks {
    public static final BlockSignal BLOCK_SIGNAL = new BlockSignal(RailWorks.MODID, "blocksignal");
    public static final BlockController BLOCK_CONTROLLER = new BlockController(RailWorks.MODID, "blockcontroller");

    public static void register() {}
}
