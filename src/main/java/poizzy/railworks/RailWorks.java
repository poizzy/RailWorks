package poizzy.railworks;

import cam72cam.mod.ModCore;
import cam72cam.mod.ModEvent;
import cam72cam.mod.render.BlockRender;
import cam72cam.mod.render.ItemRender;
import cam72cam.mod.render.StandardModel;
import poizzy.railworks.items.ItemTabs;
import poizzy.railworks.registry.DefinitionManager;
import poizzy.railworks.render.BlockModel;
import poizzy.railworks.render.TileBlockRenderer;
import poizzy.railworks.render.item.ItemSignalRender;
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
                ItemRender.register(RWItems.ITEM_SIGNAL, new ItemSignalRender());
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
