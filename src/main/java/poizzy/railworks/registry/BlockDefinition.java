package poizzy.railworks.registry;

import cam72cam.mod.ModCore;
import cam72cam.mod.resource.Identifier;
import cam72cam.mod.serialization.SerializationException;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.serialization.TagMapped;
import cam72cam.mod.serialization.TagMapper;
import com.google.gson.JsonObject;
import poizzy.railworks.render.BlockModel;
import poizzy.railworks.tile.TileBlock;

@TagMapped(BlockDefinition.TagMapper.class)
public class BlockDefinition {
    private final Class<? extends TileBlock> type;

    public String name;
    public String defId;
    public Identifier modelLoc;
    public BlockModel<?, ?> model;

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

    private void loadData(JsonObject data) {
        this.name = data.get("name").getAsString();
        this.modelLoc = new Identifier(data.get("model").getAsString());
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
