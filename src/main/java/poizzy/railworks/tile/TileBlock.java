package poizzy.railworks.tile;

import cam72cam.mod.block.BlockEntityTickable;
import cam72cam.mod.entity.boundingbox.IBoundingBox;
import cam72cam.mod.item.CustomItem;
import cam72cam.mod.item.Fuzzy;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.serialization.StrictTagMapper;
import cam72cam.mod.serialization.TagCompound;
import cam72cam.mod.serialization.TagField;
import poizzy.railworks.RWBlocks;
import poizzy.railworks.RWItems;
import poizzy.railworks.RailWorks;
import poizzy.railworks.items.ItemSignal;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.registry.DefinitionManager;
import poizzy.railworks.render.TileBlockRenderer;

import java.util.Map;

public class TileBlock extends BlockEntityTickable {
    @TagField
    private String defId;
    @TagField(value = "texture", mapper = StrictTagMapper.class)
    private String texture = null;
    @TagField
    private float angle = 0;
    @TagField
    private String state;
    @TagField
    public int ticksExisted = 0;
    @TagField
    public int start;
    @TagField
    public int end;

    @Override
    public void update() {
        ticksExisted += 1;
    }

    public TileBlock setup(String defId, String texture, double angle) {
        this.defId = defId;
        this.texture = texture;
        this.angle = (float) angle;

        placeGhostBlocks();

        return this;
    }

    @Override
    public ItemStack onPick() {
        // shouldn't be hit
        return Fuzzy.NAME_TAG.example();
    }

    @Override
    public void onBreak() {
        super.onBreak();
        TileBlockRenderer.removeRenderer(getPos());

        Vec3i pos = getPos();
        for (long i = start; i <= end; i++) {;
            getWorld().breakBlock(new Vec3i(pos.x, pos.y + i, pos.z));
        }
    }

    public void placeGhostBlocks() {
        BlockDefinition def = getDefinition();
        Vec3d max = def.model.maxOfGroup(def.model.groups());
        Vec3d min = def.model.minOfGroup(def.model.groups());
        start = (int) min.y;
        end = (int) max.y;
        Vec3i pos = getPos();
        for (int i = start; i <= end; i++) {
            if (i == 0) continue;
            getWorld().setBlock(new Vec3i(pos.x, pos.y + i, pos.z), RWBlocks.COLLISION_BLOCK);
        }
    }

    public BlockDefinition getDefinition() {
        return DefinitionManager.getDefinition(defId);
    }

    public String getTexture() {
        return this.texture;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setState(String stateName) {
        this.state = stateName;
        this.texture = getDefinition().signalStates.get(stateName).texture;
    }

    public String getState () {
        return state;
    }

    @Override
    public IBoundingBox getRenderBoundingBox() {
        BlockDefinition def = getDefinition();
        Vec3d min = def.model.minOfGroup(def.model.groups());
        Vec3d max = def.model.maxOfGroup(def.model.groups());
        return IBoundingBox.from(min, max);
    }

    @Override
    public IBoundingBox getBoundingBox() {
        BlockDefinition def = getDefinition();
        Vec3d min = def.model.minOfGroup(def.model.groups());
        Vec3d max = def.model.maxOfGroup(def.model.groups());
        return IBoundingBox.from(new Vec3d(0, min.y, 0), new Vec3d(1, max.y, 1));
    }

    public float getAngle() {
        return this.angle;
    }
}
