package lych.soulcraft.util;

import com.google.common.collect.ImmutableSet;
import lych.soulcraft.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class SoulLandGenHelper {
    private static final Set<Block> REPLACEABLE_BLOCKS = new HashSet<>(Arrays.asList(ModBlocks.PARCHED_SOIL, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, ModBlocks.SOUL_STONE));

    private SoulLandGenHelper() {}

    public static ImmutableSet<Block> getReplaceableBlocks() {
        return ImmutableSet.copyOf(REPLACEABLE_BLOCKS);
    }

    public static void registerReplaceableBlock(Block block) {
        REPLACEABLE_BLOCKS.add(block);
    }
}
