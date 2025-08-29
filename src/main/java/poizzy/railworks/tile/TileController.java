package poizzy.railworks.tile;

import cam72cam.mod.entity.Player;
import cam72cam.mod.entity.boundingbox.IBoundingBox;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.serialization.SerializationException;
import cam72cam.mod.serialization.TagCompound;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.serialization.TagMapper;
import cam72cam.mod.text.PlayerMessage;
import cam72cam.mod.util.Facing;
import cam72cam.mod.world.World;
import poizzy.railworks.RWItems;
import poizzy.railworks.items.ItemController;
import poizzy.railworks.library.GuiTypes;

import java.util.HashMap;
import java.util.Map;

public class TileController extends TileBlock {
    @TagField
    private TileSignal connectedSignal = null;
    @TagField(mapper = StateTagMapper.class)
    private Map<Integer, String> redstoneStateMap = new HashMap<>();
    @TagField
    private String currentSignalState = "DEFAULT";
    @TagField
    private World world;

    @Override
    public void update() {
        super.update();

        if (world == null) {
            world = getWorld();
        }

        if (connectedSignal == null) {
            return;
        }

        String newState = redstoneStateMap.getOrDefault(getWorld().getRedstone(getPos()), "DEFAULT");
        if (!newState.equals(currentSignalState)) {
            connectedSignal.setState(newState);
            currentSignalState = newState;
            connectedSignal.markDirty();
        }
    }

    @Override
    public ItemStack onPick() {
        ItemStack stack = new ItemStack(RWItems.ITEM_CONTROLLER, 1);
        ItemController.Data data = new ItemController.Data(stack);
        data.block = getDefinition();
        data.texture = this.getTexture();
        data.write();
        return stack;
    }

    public Map<Integer, String> getRedstoneStateMap() {
        return this.redstoneStateMap;
    }

    public void setRedstoneState(Map<Integer, String> redstoneStateMap) {
        this.redstoneStateMap = redstoneStateMap;
    }

    @Override
    public boolean onClick(Player player, Player.Hand hand, Facing facing, Vec3d hit) {
        if (player.getHeldItem(hand).is(RWItems.ITEM_CONTROLLER)) {
            return false;
        }

        if (connectedSignal != null) {
            GuiTypes.CONTROLLER.open(player, getPos());
        } else {
            player.sendMessage(PlayerMessage.direct("No signal connected! Connect signal first!"));
            return false;
        }
        return true;
    }

    public boolean hasSignalState(String state) {
        return connectedSignal != null && connectedSignal.getDefinition().signalStates.containsKey(state);
    }

    @Override
    public IBoundingBox getBoundingBox() {
        return IBoundingBox.BLOCK;
    }

    public void connectSignal(TileSignal signal) {
        this.connectedSignal = signal;
    }

    public void removeSignal() {
        this.connectedSignal = null;
    }

    public TileSignal getConnectedSignal() {
        return this.connectedSignal;
    }

    public static class StateTagMapper implements TagMapper<Map<Integer, String>> {

        @Override
        public TagAccessor<Map<Integer, String>> apply(Class<Map<Integer, String>> type, String fieldName, TagField tag) throws SerializationException {
            return new TagAccessor<>(
                    (d, o) -> d.setMap(fieldName, o, String::valueOf, v -> new TagCompound().setString("s", v)),
                    d -> d.getMap(fieldName, Integer::parseInt, v -> v.getString("s"))
            );
        }
    }
}
