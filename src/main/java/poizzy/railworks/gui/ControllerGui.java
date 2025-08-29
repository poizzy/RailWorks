package poizzy.railworks.gui;

import cam72cam.mod.gui.helpers.GUIHelpers;
import cam72cam.mod.gui.screen.IScreen;
import cam72cam.mod.gui.screen.IScreenBuilder;
import cam72cam.mod.render.opengl.RenderState;
import cam72cam.mod.resource.Identifier;
import poizzy.railworks.RailWorks;
import poizzy.railworks.gui.components.CustomButton;
import poizzy.railworks.gui.components.ListSelector;
import poizzy.railworks.library.CircularList;
import poizzy.railworks.library.ControllerPacket;
import poizzy.railworks.render.BlockModel;
import poizzy.railworks.tile.TileController;
import poizzy.railworks.tile.TileSignal;

import java.util.*;
import java.util.stream.Collectors;

public class ControllerGui implements IScreen {
    private static final Identifier GUI_TEXTURE = new Identifier(RailWorks.MODID, "textures/gui/controller.png");
    private static final int GUI_WIDTH = 248;
    private static final int GUI_HEIGHT = 166;
    private static final int REFRESH_RATE = 60;

    private ListSelector listSelector;
    private int frame = 0;
    private final TileController controller;

    public ControllerGui(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void init(IScreenBuilder screen) {

        // Prefill lists;
        Map<Integer, String> map = controller.getRedstoneStateMap();

        int xMiddle = screen.getWidth() / 2;
        int yMiddle = screen.getHeight() / 2;

        int xStart = xMiddle - 114;
        int yStart = yMiddle - 68;

        this.listSelector = new ListSelector(controller.getConnectedSignal(), xStart, yStart);
        if (!map.isEmpty()) {
            map.forEach((i, s) -> listSelector.addLine(i, s));
        } else {
            listSelector.addLine(0, "");
        }
    }

    @Override
    public void onEnterKey(IScreenBuilder builder) {
        builder.close();
    }

    @Override
    public void onClose() {
        listSelector.dispose();
    }

    @Override
    public void draw(IScreenBuilder builder, RenderState state) {
        frame++;

        int x = (builder.getWidth() - GUI_WIDTH) / 2;
        int y = (builder.getHeight() - GUI_HEIGHT) / 2;

        GUIHelpers.texturedRect(GUI_TEXTURE, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT, GUI_WIDTH, GUI_HEIGHT, 256, 256);

        TileSignal signal = controller.getConnectedSignal();
        BlockModel<?, ?> model = signal.getDefinition().model;

        listSelector.draw(builder, state);

        if (frame >= REFRESH_RATE) {
            new ControllerPacket(controller.getWorld(), controller.getPos(), listSelector.getRedstoneLevels()).sendToServer();
            frame = 0;
        }


        state.translate(((double) builder.getWidth() / 2) + 90d, ((double) builder.getHeight() / 2) + 60d, 400);
        state.scale(-16, -16, -16);
        state.lightmap(1, 1);
        model.renderBlock(signal, state, 0);
    }
}
