package poizzy.railworks.library;

import cam72cam.mod.gui.GuiRegistry;
import cam72cam.mod.gui.GuiRegistry.BlockGUI;
import poizzy.railworks.gui.ControllerGui;
import poizzy.railworks.tile.TileController;

public class GuiTypes {
    public static final BlockGUI CONTROLLER = GuiRegistry.registerBlock(TileController.class, ControllerGui::new);

    public static void register() {

    }
}
