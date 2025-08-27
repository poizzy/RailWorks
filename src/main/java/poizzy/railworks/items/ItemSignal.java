package poizzy.railworks.items;

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
import poizzy.railworks.RWBlocks;
import poizzy.railworks.RailWorks;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.registry.DefinitionManager;
import poizzy.railworks.registry.SignalDefinition;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.tile.TileSignal;

import java.util.*;

public class ItemSignal extends CustomItem {
    public ItemSignal() {
        super(RailWorks.MODID, "item_signal");
    }
    @Override
    public int getStackSize() {
        return 1;
    }

    @Override
    public List<CreativeTab> getCreativeTabs() {
        return Collections.singletonList(ItemTabs.SIGNAL_TAB);
    }

    @Override
    public String getCustomName(ItemStack stack) {
        Data data = new Data(stack);
        if (data.block == null) {
            return "";
        }
        return data.block.name;
    }

    @Override
    public List<ItemStack> getItemVariants(CreativeTab creativeTab) {
        if (!Objects.equals(creativeTab, ItemTabs.SIGNAL_TAB)) {
            return Collections.emptyList();
        }

        List<ItemStack> items = new ArrayList<>();
        for (BlockDefinition def : DefinitionManager.getDefinitions()) {
            if (!(def instanceof SignalDefinition)) {
                continue;
            }
            ItemStack stack = new ItemStack(this, 1);
            Data data = new Data(stack);
            data.block = def;
            data.write();
            items.add(stack);
        }
        return items;
    }

    @Override
    public ClickResult onClickBlock(Player player, World world, Vec3i pos, Player.Hand hand, Facing facing, Vec3d inBlockPos) {
        if (world.isClient) {
            return ClickResult.ACCEPTED;
        }

        Vec3i target = world.isReplaceable(pos) ? pos : pos.offset(facing);

        if (world.isAir(target) || world.isReplaceable(target)) {
            Data data = new Data(player.getHeldItem(hand));
            world.setBlock(target, RWBlocks.BLOCK_SIGNAL);

            TileBlock te = world.getBlockEntity(target, TileBlock.class);

            if (te instanceof TileSignal) {
                float rotationSteps = 15;
                int rotation = (int) (-(Math.round(player.getRotationYawHead() / rotationSteps) * rotationSteps) + 90);
                te.setup(data.block.defId, data.texture, rotation);
                te.markDirty();
            }

            return ClickResult.ACCEPTED;
        }

        return ClickResult.REJECTED;
    }

    public static class Data extends ItemDataSerializer {
        @TagField
        public BlockDefinition block;
        public String texture = null;

        public Data(ItemStack stack) {
            super(stack);
        }
    }
}
