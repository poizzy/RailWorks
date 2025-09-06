package poizzy.railworks.tile;

import cam72cam.mod.entity.sync.TagSync;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.serialization.TagField;
import poizzy.railworks.RWItems;
import poizzy.railworks.items.ItemSignal;
import poizzy.railworks.registry.SignalDefinition;

public class TileSignal extends TileBlock {
    @TagSync
    @TagField
    private Vec3i controller;

    public void setController(Vec3i pos) {
        this.controller = pos;
    }

    public void removeController() {
        controller = null;
    }

    @Override
    public SignalDefinition getDefinition() {
        return (SignalDefinition) super.getDefinition();
    }

    @Override
    public void onBreak() {
        super.onBreak();
        if (controller != null) {
            TileController ctrl = getWorld().getBlockEntity(controller, TileController.class);
            ctrl.removeSignal();
        }

        removeController();
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
