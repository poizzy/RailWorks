package poizzy.railworks.registry;

import com.google.gson.JsonObject;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.tile.TileSignal;

public class SignalDefinition extends BlockDefinition{
    public SignalDefinition(Class<? extends TileBlock> type, String defId, JsonObject json) {
        super(type, defId, json);
    }

    public SignalDefinition(String defId, JsonObject json) {
        this(TileSignal.class, defId, json);
    }
}
