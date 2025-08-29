package poizzy.railworks.tile;

import cam72cam.mod.item.ItemStack;
import poizzy.railworks.RWItems;
import poizzy.railworks.items.ItemSignal;
import poizzy.railworks.registry.SignalDefinition;

public class TileSignal extends TileBlock {

    @Override
    public SignalDefinition getDefinition() {
        return (SignalDefinition) super.getDefinition();
    }

    @Override
    public ItemStack onPick() {
        ItemStack stack = new ItemStack(RWItems.ITEM_SIGNAL, 1);
        ItemSignal.Data data = new ItemSignal.Data(stack);
        data.block = getDefinition();
        data.texture = this.getTexture();
        data.write();
        return stack;
    }

    @Override
    public void update() {
        super.update();
    }
}
