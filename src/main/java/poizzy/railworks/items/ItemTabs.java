package poizzy.railworks.items;

import cam72cam.mod.item.CreativeTab;
import cam72cam.mod.item.Fuzzy;
import cam72cam.mod.item.ItemStack;
import poizzy.railworks.RailWorks;

public class ItemTabs {
    public static CreativeTab SIGNAL_TAB;
    public static CreativeTab CONTROLLER_TAB;

    static {
        SIGNAL_TAB = new CreativeTab(RailWorks.MODID + ".signals", Fuzzy.BUCKET::example, true);
        CONTROLLER_TAB = new CreativeTab(RailWorks.MODID + ".controller", Fuzzy.BOOK::example);
    }

    public static void register() {

    }
}
