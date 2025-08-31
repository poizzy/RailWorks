package poizzy.railworks.items;

import cam72cam.mod.block.BlockType;
import cam72cam.mod.entity.Player;
import cam72cam.mod.item.ClickResult;
import cam72cam.mod.item.CreativeTab;
import cam72cam.mod.item.CustomItem;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.serialization.TagField;
import cam72cam.mod.util.Facing;
import cam72cam.mod.world.World;
import poizzy.railworks.RailWorks;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.tile.TileController;
import poizzy.railworks.tile.TileSignal;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static poizzy.railworks.library.ChatText.*;

public class ItemConnector extends CustomItem {
    public ItemConnector() {
        super(RailWorks.MODID, "item_connector");
    }

    @Override
    public List<CreativeTab> getCreativeTabs() {
        return Collections.singletonList(ItemTabs.CONTROLLER_TAB);
    }

    @Override
    public String getCustomName(ItemStack stack) {
        return "Connector";
    }

    @Override
    public List<ItemStack> getItemVariants(CreativeTab creativeTab) {
        if (creativeTab == null || creativeTab.equals(ItemTabs.CONTROLLER_TAB)) {
            return Collections.singletonList(new ItemStack(this, 1));
        }
        return Collections.emptyList();
    }

    @Override
    public ClickResult onClickBlock(Player player, World world, Vec3i pos, Player.Hand hand, Facing facing, Vec3d inBlockPos) {
        TileBlock clicked = world.getBlockEntity(pos, TileBlock.class);
        if (clicked == null) {
            BlockType block = world.getBlock(pos).getBlock();
            if (!block.id.getPath().equals("collision")) {
                return ClickResult.REJECTED;
            }
            for (int i = pos.y; i >= 0; i--) {
                clicked = world.getBlockEntity(new Vec3i(pos.x, i, pos.z), TileBlock.class);
                if (clicked != null) break;
            }

            if (clicked == null) return ClickResult.REJECTED;
        }

        ItemStack stack = player.getHeldItem(hand);
        Data data = new Data(stack);

        if (player.isCrouching() && clicked instanceof TileController) {
            if (((TileController) clicked).getConnectedSignal() != null) {
                ((TileController) clicked).removeSignal();
                player.sendMessage(CONTROLLER_CLEARED.getMessage());
                return ClickResult.ACCEPTED;
            }
            return ClickResult.REJECTED;
        }

        if (data.firstBlock == null) {
            data.firstBlock = pos;
            data.firstDim = world.getId();
            data.write();
            return ClickResult.ACCEPTED;
        }

        if (world.getId() != data.firstDim) {
            player.sendMessage(CONNECTOR_DIM_MISMATCH.getMessage());
            clearSelection(data);
            return ClickResult.REJECTED;
        }

        if (pos.equals(data.firstBlock)) {
            clearSelection(data);
            player.sendMessage(CONNECTOR_SAME_BLOCK.getMessage());
            return ClickResult.REJECTED;
        }

        TileBlock a = world.getBlockEntity(data.firstBlock, TileBlock.class);
        TileBlock b = clicked;
        if (a == null || b == null) {
            clearSelection(data);
            player.sendMessage(CONNECTOR_TILE_MISSING.getMessage());
            return ClickResult.REJECTED;
        }

        TileSignal signal = a instanceof TileSignal ? (TileSignal) a : (b instanceof TileSignal ? (TileSignal) b : null);
        TileController controller = a instanceof TileController ? (TileController) a : (b instanceof TileController ? (TileController) b : null);

        if (signal == null || controller == null) {
            clearSelection(data);
            player.sendMessage(CONNECTOR_INVALID_PAIR.getMessage());
            return ClickResult.REJECTED;
        }

        if (controller.getConnectedSignal() != null) {
            player.sendMessage(CONNECTOR_CONTROLLER_BUSY.getMessage());
            return ClickResult.REJECTED;
        }

        controller.connectSignal(signal);

        clearSelection(data);

        player.sendMessage(CONNECTOR_CONNECTED.getMessage(signal.getDefinition().name));

        return ClickResult.ACCEPTED;
    }

    private void clearSelection(Data data) {
        data.firstBlock = null;
        data.secondBlock = null;
        data.firstDim = null;
        data.write();
    }

    public static class Data extends ItemDataSerializer {
        @TagField
        public Vec3i firstBlock;
        @TagField
        public Vec3i secondBlock;
        @TagField
        public Integer firstDim;

        protected Data(ItemStack stack) {
            super(stack);
        }
    }
}
