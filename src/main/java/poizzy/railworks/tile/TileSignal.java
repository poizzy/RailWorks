package poizzy.railworks.tile;

import poizzy.railworks.registry.SignalDefinition;

public class TileSignal extends TileBlock {

    @Override
    public SignalDefinition getDefinition() {
        return (SignalDefinition) super.getDefinition();
    }

    @Override
    public void update() {
        super.update();

        if (getWorld().getRedstone(getPos()) == 15) {
            this.setState("hp 0");
        } else {
            this.setState("DEFAULT");
        }
    }
}
