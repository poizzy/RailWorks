package poizzy.railworks.animation;

import cam72cam.mod.math.Vec3i;
import poizzy.railworks.registry.BlockDefinition;
import poizzy.railworks.tile.TileBlock;
import poizzy.railworks.library.ExpireableMap;
import util.Matrix4;

import java.io.IOException;
import java.util.Objects;

public class BlockAnimation {
    private final BlockDefinition.States signalState;
    private final Animatrix animatrix;
    private final ExpireableMap<Vec3i, Boolean> active;
    private final ExpireableMap<Vec3i, Float> tickStart;
    private final ExpireableMap<Vec3i, Float> tickStop;

    public BlockAnimation(BlockDefinition.States states) throws IOException {
        this.signalState = states;
        this.animatrix = new Animatrix(states.animation.getResourceStream(), 1);
        tickStart = new ExpireableMap<>();
        tickStop = new ExpireableMap<>();
        active = new ExpireableMap<>();
    }

    public float getValue(TileBlock te) {
        String currentState = te.getState();
        String expectedState = signalState.stateName;
        return (float) (Objects.equals(currentState, expectedState) ? 1 : 0);
    }

    public float getPercent(TileBlock te, float partialTicks) {
        float value = getValue(te);

        float total_tick_per_loop = animatrix.frameCount();

        float tickCount = te.ticksExisted + partialTicks;

        Vec3i key = te.getPos();
        float tickDelta;
        if (value >= 0.95) {
            if (tickStart.get(key) == null) {
                if (active.get(key) == null) {
                    active.put(key, true);
                    tickStart.put(key, tickCount - total_tick_per_loop - 1);
                } else {
                    tickStart.put(key, tickCount);
                }
                tickStop.remove(key);
            }
            tickDelta = tickCount - tickStart.get(key);
        } else {
            if (tickStop.get(key) == null) {
                if (active.get(key) == null) {
                    active.put(key, true);
                    tickStop.put(key, tickCount - total_tick_per_loop - 1);
                } else {
                    tickStop.put(key, tickCount);
                }
                tickStart.remove(key);
            }
            tickDelta = tickCount - tickStop.get(key);
            tickDelta = total_tick_per_loop - tickDelta;
        }

        return tickDelta / total_tick_per_loop;
    }

    public Matrix4 getMatrix(TileBlock te, String group, float partialTicks) {
        return animatrix.groups().contains(group) ? animatrix.getMatrix(group, getPercent(te, partialTicks), false) : null;
    }
}
