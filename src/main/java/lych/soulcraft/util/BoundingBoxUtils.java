package lych.soulcraft.util;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.ArrayList;
import java.util.List;

public final class BoundingBoxUtils {
    private BoundingBoxUtils() {}

    public static Vector3d bottomOf(AxisAlignedBB bb) {
        return new Vector3d(MathHelper.lerp(0.5, bb.minX, bb.maxX), bb.minY, MathHelper.lerp(0.5, bb.minZ, bb.maxZ));
    }

    public static List<BlockPos> getBlockPosInside(AxisAlignedBB bb) {
        List<BlockPos> list = new ArrayList<>(MathHelper.ceil(getVolumeOf(bb)));
        for (int x = MathHelper.floor(bb.minX); x <= MathHelper.floor(bb.maxX); x++) {
            for (int y = MathHelper.floor(bb.minY); y <= MathHelper.floor(bb.maxY); y++) {
                for (int z = MathHelper.floor(bb.minZ); z <= MathHelper.floor(bb.maxZ); z++) {
                    list.add(new BlockPos(x, y, z));
                }
            }
        }
        return list;
    }

    public static double getVolumeOf(AxisAlignedBB bb) {
        return bb.getXsize() * bb.getYsize() * bb.getZsize();
    }

    public static AxisAlignedBB inflate(Vector3d vector3d, double length) {
        return inflate(vector3d, length, length, length);
    }

    public static AxisAlignedBB inflate(Vector3d vector3d, double x, double y, double z) {
        return new AxisAlignedBB(vector3d.subtract(x, y, z), vector3d.add(x, y, z));
    }
}
