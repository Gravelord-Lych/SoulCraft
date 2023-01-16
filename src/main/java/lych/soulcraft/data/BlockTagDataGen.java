package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeBlockTagsProvider;

import static lych.soulcraft.block.ModBlocks.*;

public class BlockTagDataGen extends ForgeBlockTagsProvider {
    public BlockTagDataGen(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, existingFileHelper);
    }

    @Override
    public void addTags() {
        tag(BlockTags.BEACON_BASE_BLOCKS).add(REFINED_SOUL_METAL_BLOCK,
                SOUL_METAL_BLOCK);
        tag(BlockTags.SOUL_FIRE_BASE_BLOCKS).add(CRACKED_SOUL_STONE_BRICK_SLAB,
                CRACKED_SOUL_STONE_BRICK_STAIRS,
                CRACKED_SOUL_STONE_BRICK_WALL,
                CRACKED_SOUL_STONE_BRICKS,
                DECAYED_STONE,
                DECAYED_STONE_BRICK_SLAB,
                DECAYED_STONE_BRICK_STAIRS,
                DECAYED_STONE_BRICK_WALL,
                DECAYED_STONE_SLAB,
                DECAYED_STONE_STAIRS,
                DECAYED_STONE_WALL,
                REFINED_SOUL_METAL_BLOCK,
                SMOOTH_SOUL_STONE,
                SMOOTH_SOUL_STONE_SLAB,
                SMOOTH_SOUL_STONE_STAIRS,
                SMOOTH_SOUL_STONE_WALL,
                SOUL_METAL_BLOCK,
                SOUL_STONE,
                SOUL_STONE_BRICK_SLAB,
                SOUL_STONE_BRICK_STAIRS,
                SOUL_STONE_BRICK_WALL,
                SOUL_STONE_BRICKS,
                SOUL_STONE_SLAB,
                SOUL_STONE_STAIRS,
                SOUL_STONE_WALL);
        tag(BlockTags.WALLS).add(CRACKED_DECAYED_STONE_BRICK_WALL,
                CRACKED_SOUL_STONE_BRICK_WALL,
                DECAYED_STONE_BRICK_WALL,
                DECAYED_STONE_WALL,
                SMOOTH_SOUL_STONE_WALL,
                SOUL_STONE_BRICK_WALL,
                SOUL_STONE_WALL);
    }

    @Override
    public String getName() {
        return "Block Tags: " + SoulCraft.MOD_ID;
    }
}
