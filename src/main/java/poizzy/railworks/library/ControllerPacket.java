package poizzy.railworks.library;

import cam72cam.mod.math.Vec3i;
import cam72cam.mod.net.Packet;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.world.World;
import poizzy.railworks.tile.TileController;

import java.util.Map;

public class ControllerPacket extends Packet {
    @TagField
    public World world;
    @TagField
    public Vec3i pos;
    @TagField(mapper = TileController.StateTagMapper.class)
    public Map<Integer, String> redstoneLevels;

    public ControllerPacket() {
    }

    public ControllerPacket(World world, Vec3i pos, Map<Integer, String> redstoneLevels) {
        this.world = world;
        this.pos = pos;
        this.redstoneLevels = redstoneLevels;
    }

    @Override
    protected void handle() {
        TileController tile = world.getBlockEntity(pos, TileController.class);
        tile.setRedstoneState(redstoneLevels);
        new ControllerClientPacket(world, pos, redstoneLevels).sendToAll();
    }
}

