package poizzy.railworks;

import cam72cam.mod.ModCore;
import cam72cam.mod.ModEvent;
import cam72cam.mod.event.ClientEvents;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.net.Packet;
import cam72cam.mod.net.PacketDirection;
import cam72cam.mod.render.BlockRender;
import cam72cam.mod.render.ItemRender;
import cam72cam.mod.resource.Identifier;
import poizzy.railworks.items.ItemTabs;
import poizzy.railworks.library.ControllerClientPacket;
import poizzy.railworks.library.ControllerPacket;
import poizzy.railworks.library.GuiTypes;
import poizzy.railworks.library.MouseHelper;
import poizzy.railworks.registry.DefinitionManager;
import poizzy.railworks.render.ObjItemRender;
import poizzy.railworks.render.TileBlockRenderer;
import poizzy.railworks.render.item.DefaultItemRenderer;
import poizzy.railworks.tile.TileController;
import poizzy.railworks.tile.TileSignal;

import java.io.IOException;

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
                ItemTabs.register();

                GuiTypes.register();
                RWBlocks.register();
                RWItems.register();

                Packet.register(ControllerPacket::new, PacketDirection.ClientToServer);
                Packet.register(ControllerClientPacket::new, PacketDirection.ServerToClient);
                break;
            case INITIALIZE:
                try {
                    DefinitionManager.initDefinitions();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                BlockRender.register(RWBlocks.BLOCK_SIGNAL, TileBlockRenderer::render, TileSignal.class);
                BlockRender.register(RWBlocks.BLOCK_CONTROLLER, TileBlockRenderer::render, TileController.class);

                ItemRender.register(RWItems.ITEM_CONTROLLER, ObjItemRender.getModelFor(new Identifier(RailWorks.MODID, "models/empty/empty_cube.obj"), new Vec3d(0.5, 0.5, 0.5), 0.5f));
                ItemRender.register(RWItems.ITEM_CONNECTOR, ObjItemRender.getModelFor(new Identifier(RailWorks.MODID, "models/empty/empty_plane.obj"), new Vec3d(0.5, 0.5, 0.5), 0.5f));
                ItemRender.register(RWItems.ITEM_SIGNAL, new DefaultItemRenderer());
                break;
            case INITIALIZE:
                break;
            case SETUP:
                ClientEvents.MOUSE_GUI.subscribe(evt -> {
                    MouseHelper.onMouseEvent(evt);
                    return true;
                });
                break;
            case FINALIZE:
                break;
            case START:
                break;
            case RELOAD:
                try {
                    DefinitionManager.initDefinitions();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
