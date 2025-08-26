package poizzy.railworks;

import cam72cam.mod.ModCore;
import cam72cam.mod.ModEvent;

public class RailWorks extends ModCore.Mod {
    public static final String MODID = "railworks";

    @Override
    public String modID() {
        return MODID;
    }

    @Override
    public void commonEvent(ModEvent event) {
        switch (event) {
            case CONSTRUCT:
                break;
            case INITIALIZE:
                break;
            case SETUP:
                break;
            case FINALIZE:
                break;
            case START:
                break;
            case RELOAD:
                break;
        }
    }

    @Override
    public void clientEvent(ModEvent event) {
        switch (event) {
            case CONSTRUCT:
                break;
            case INITIALIZE:
                break;
            case SETUP:
                break;
            case FINALIZE:
                break;
            case START:
                break;
            case RELOAD:
                break;
        }
    }

    @Override
    public void serverEvent(ModEvent event) {
        switch (event) {
            case CONSTRUCT:
                break;
            case INITIALIZE:
                break;
            case SETUP:
                break;
            case FINALIZE:
                break;
            case START:
                break;
            case RELOAD:
                break;
        }
    }
}
