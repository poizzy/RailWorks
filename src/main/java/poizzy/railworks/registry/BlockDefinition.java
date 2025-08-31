package poizzy.railworks.registry;

import cam72cam.mod.ModCore;
import cam72cam.mod.resource.Identifier;
import cam72cam.mod.serialization.SerializationException;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.serialization.TagMapped;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import poizzy.railworks.render.BlockModel;
import poizzy.railworks.tile.TileBlock;

import java.util.*;

@TagMapped(BlockDefinition.TagMapper.class)
public class BlockDefinition {
    private final Class<? extends TileBlock> type;

    public String name;
    public String defId;
    public Identifier modelLoc;
    public BlockModel<?, ?> model;

    public Map<String, States> signalStates;

    public static class States {
        public String texture;
        public Identifier animation;
        public String stateName;
        public List<String> controlGroups;

        public States(JsonObject obj, String stateName) {
            Optional.ofNullable(obj.get("texture")).ifPresent(o -> this.texture = o.getAsString());
            Optional.ofNullable(obj.get("animation")).ifPresent(o -> this.animation = new Identifier(o.getAsString()));

            controlGroups = new ArrayList<>();
            Optional.ofNullable(obj.get("controlGroups")).ifPresent(o -> o.getAsJsonArray().forEach(a -> controlGroups.add(a.getAsString())));
            this.stateName = stateName;
        }

        public States(String texture) {
            this.texture = texture;
            controlGroups = new ArrayList<>();
        }
    }

    public BlockDefinition(Class<? extends TileBlock> type, String defId, JsonObject json) {
        this.type = type;
        this.defId = defId;

        loadData(json);

        try {
            this.model = new BlockModel<>(this);
        } catch (Exception e) {
            this.model = null;
            ModCore.catching(e);
        }
    }

    protected void loadData(JsonObject data) {
        this.name = data.get("name").getAsString();
        this.modelLoc = new Identifier(data.get("model").getAsString());

        signalStates = new LinkedHashMap<>();
        signalStates.put("DEFAULT", new States(""));

        JsonElement states = data.get("states");

        if (states != null && states.isJsonObject()) {
            for (Map.Entry<String, JsonElement> elements : states.getAsJsonObject().entrySet()) {
                String state = elements.getKey();
                States options = new States(elements.getValue().getAsJsonObject(), state);
                signalStates.put(state, options);
            }
        }
    }

    static class TagMapper implements cam72cam.mod.serialization.TagMapper<BlockDefinition> {

        @Override
        public TagAccessor<BlockDefinition> apply(Class<BlockDefinition> type, String fieldName, TagField tag) throws SerializationException {
            return new TagAccessor<>(
                    (d, o) -> d.setString(fieldName, o == null ? null : o.defId),
                    d -> DefinitionManager.getDefinition(d.getString(fieldName))
            );
        }
    }
}
