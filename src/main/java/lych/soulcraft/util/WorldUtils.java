package lych.soulcraft.util;

import com.google.common.base.Preconditions;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class WorldUtils {
    private WorldUtils() {}

    public static List<BlockPos> getNearbyBlocks(BlockPos pos) {
        return getNearbyBlocks(pos, 1);
    }

    public static List<BlockPos> getNearbyBlocks(BlockPos pos, int radius) {
        Preconditions.checkArgument(radius > 0, "Radius must be positive");
        List<BlockPos> blocks = new ArrayList<>((radius + 2) * (radius + 2) * (radius + 2));
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
//                  Exclude self
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }
                    blocks.add(pos.offset(x, y, z));
                }
            }
        }
        return blocks;
    }
}
