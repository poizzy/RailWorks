package poizzy.railworks.registry;

import cam72cam.mod.resource.Identifier;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import poizzy.railworks.RailWorks;
import poizzy.railworks.blocks.BlockSignal;
import poizzy.railworks.tile.TileSignal;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DefinitionManager {
    private enum BlockLoaders {
        SIGNAL(SignalDefinition::new);

        final BiFunction<String, JsonObject, BlockDefinition> definitions;
        BlockLoaders(BiFunction<String, JsonObject, BlockDefinition> provider) {
            this.definitions = provider;
        }

        public BlockDefinition load(String defId, JsonObject json) {
            return definitions.apply(defId, json);
        }
    }

    private static final Map<String, BlockDefinition> blocks = new LinkedHashMap<>();

    public static void initDefinitions() throws IOException {
        Identifier blockDefinitions = new Identifier(RailWorks.MODID, "blocks.json");
        List<InputStream> streams = blockDefinitions.getResourceStreamAll();
        List<JsonObject> blockTypes = new ArrayList<>();

        for (InputStream stream : streams) {
            JsonObject obj = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
            stream.close();
            blockTypes.add(obj);
        }

        for (JsonObject blockDef : blockTypes) {
            for (Map.Entry<String, JsonElement> defTypes : blockDef.entrySet()) {
                BlockLoaders loaders;
                String type = defTypes.getKey().toUpperCase();
                try {
                    loaders = BlockLoaders.valueOf(type);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(e);
                }
                JsonArray defNames = defTypes.getValue().getAsJsonArray();

                for (int i = 0; i < defNames.size(); i++) {
                    String defName = defNames.get(i).getAsString();
                    String defPath = String.format("blocks/%s/%s.json", type, defName);

                    if (blocks.containsKey(defPath)) {
                        continue;
                    }

                    Identifier defIdent = new Identifier(RailWorks.MODID, defPath);

                    try (InputStream stream = defIdent.getResourceStream()) {
                        JsonObject blockDefinition = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
                        ;
                        blocks.put(defPath, loaders.load(defPath, blockDefinition));
                    }
                }
            }
        }
    }

    public static BlockDefinition getDefinition(String defId) {
        return blocks.get(defId);
    }

    public static Collection<BlockDefinition> getDefinitions() {
        return blocks.values();
    }

}
