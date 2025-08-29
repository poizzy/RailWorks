package poizzy.railworks.registry;

import com.google.gson.JsonObject;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.tile.TileController;

public class ControllerDefinition extends BlockDefinition{
    public ControllerDefinition(Class<? extends TileBlock> type, String defId, JsonObject json) {
        super(type, defId, json);
    }

    public ControllerDefinition(String defId, JsonObject json) {
        this(TileController.class, defId, json);
    }


}
