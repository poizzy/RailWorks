package poizzy.railworks;

import cam72cam.mod.ModCore;

@net.minecraftforge.fml.common.Mod(modid = Mod.MODID, name = "RailWorks", version = "0.0.1", dependencies = "required-after:universalmodcore@[1.2, 1.3)", acceptedMinecraftVersions = "[1.12,1.13)")
public class Mod {
    public static final String MODID = "railworks";

    static {
        try {
            ModCore.register(new poizzy.railworks.RailWorks());
        } catch (Exception e) {
            throw new RuntimeException("Could not load mod " + MODID, e);
        }
    }
}
