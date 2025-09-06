package poizzy.railworks.blocks;

import cam72cam.mod.block.BlockType;
import cam72cam.mod.entity.Player;
import cam72cam.mod.entity.boundingbox.IBoundingBox;
import cam72cam.mod.item.Fuzzy;
import cam72cam.mod.item.ItemStack;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.math.Vec3i;
import cam72cam.mod.util.Facing;
import cam72cam.mod.world.World;
import poizzy.railworks.RailWorks;
import poizzy.railworks.render.BlockModel;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.tile.TileController;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CollisionBlock extends BlockType {
    private final Map<Vec3i, TileBlock> parentCache = new HashMap<>();

    public CollisionBlock() {
        super(RailWorks.MODID, "collision");
    }

    @Override
    public IBoundingBox getBoundingBox(World world, Vec3i pos) {
        TileBlock parent = getParent(world, pos);
        if (parent != null) {
            BlockModel<?, ?> model = parent.getDefinition().model;
            Vec3d min = model.minOfGroup(model.groups());
            Vec3d max = model.maxOfGroup(model.groups());
            min = new Vec3d(0, min.y, 0).add(0, -pos.y + parent.getPos().y, 0);
            max = new Vec3d(1, max.y, 1).add(0, -pos.y + parent.getPos().y, 0);;
            return IBoundingBox.from(min, max);
        }
        return IBoundingBox.BLOCK;
    }

    @Override
    public boolean tryBreak(World world, Vec3i pos, Player player) {
        return true;
    }

    @Override
    public void onBreak(World world, Vec3i pos) {
        if (world.isClient) return;

        TileBlock parent = getParent(world, pos);

        if (parent != null) {
            Vec3i parentPos = parent.getPos();
            world.breakBlock(parentPos);
            for (int i = parent.start; i <= parent.end; i++) {
                Vec3i blockPos = parentPos.add(0, i, 0);
                world.breakBlock(blockPos);
                parentCache.remove(blockPos);
            }
        }
    }

    @Override
    public boolean onClick(World world, Vec3i pos, Player player, Player.Hand hand, Facing facing, Vec3d hit) {
        TileBlock parent = getParent(world, pos);
        if (parent != null && parent instanceof TileController) {
            TileController controller = (TileController) parent;
            return controller.onClick(player, hand, facing, hit);
        }
        return false;
    }

    @Override
    public ItemStack onPick(World world, Vec3i pos) {
        TileBlock parent = getParent(world, pos);

        if (parent != null) {
            return parent.onPick();
        }
        return Fuzzy.NAME_TAG.example();
    }

    @Override
    public void onNeighborChange(World world, Vec3i pos, Vec3i neighbor) {

    }

    @Nullable
    public TileBlock getParent(World world, Vec3i blockPos) {
        return parentCache.computeIfAbsent(blockPos, pos -> {
            for (int i = pos.y; i >= 0; i--) {
                TileBlock parent = world.getBlockEntity(new Vec3i(pos.x, i, pos.z), TileBlock.class);
                if (parent != null) return parent;
            }
            return null;
        });
    }
}
