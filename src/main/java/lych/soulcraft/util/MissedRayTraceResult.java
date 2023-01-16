package lych.soulcraft.util;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class MissedRayTraceResult extends RayTraceResult {
    public MissedRayTraceResult(Vector3d location) {
        super(location);
    }

    @Override
    public Type getType() {
        return Type.MISS;
    }
}
