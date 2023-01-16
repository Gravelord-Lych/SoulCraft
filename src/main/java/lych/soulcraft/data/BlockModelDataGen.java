package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import lych.soulcraft.block.ModBlocks;

import java.util.Objects;

import static lych.soulcraft.data.ModDataGens.registryNameToString;

public class BlockModelDataGen extends BlockModelProvider {
    static final ModelFile CUBE_ALL = new ModelFile.UncheckedModelFile("block/cube_all");
    private static final ModelFile CUBE_COLUMN = new ModelFile.UncheckedModelFile("block/cube_column");

    static final String ALL = "all";
    private static final String PARTICLE = "particle";
    private static final String MACHINE_SIDE = "soul_machine_side";
    static final String SIMPLE_MACHINE_SIDE = "simple_soul_machine_side";
    static final String L2_MACHINE_SIDE = "soul_machine_side";
    static final String SIMPLE_L2_MACHINE_SIDE = "simple_l2_soul_machine_side";

    public BlockModelDataGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SoulCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        getBuilder(registryNameToString(ModBlocks.CHISELED_SOUL_STONE_BRICKS)).parent(CUBE_COLUMN).texture("end", prefix(ModBlocks.SMOOTH_SOUL_STONE)).texture("side", prefix(ModBlocks.CHISELED_SOUL_STONE_BRICKS));
        getBuilder(registryNameToString(ModBlocks.CRACKED_DECAYED_STONE_BRICKS)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS));
        wallInventory(wallInventoryToString(ModBlocks.CRACKED_DECAYED_STONE_BRICK_WALL), prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS));
        getBuilder(registryNameToString(ModBlocks.CRACKED_SOUL_STONE_BRICKS)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS));
        wallInventory(wallInventoryToString(ModBlocks.CRACKED_SOUL_STONE_BRICK_WALL), prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS));
        getBuilder(registryNameToString(ModBlocks.DECAYED_STONE)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.DECAYED_STONE));
        wallInventory(wallInventoryToString(ModBlocks.DECAYED_STONE_BRICK_WALL), prefix(ModBlocks.DECAYED_STONE_BRICKS));
        getBuilder(registryNameToString(ModBlocks.DECAYED_STONE_BRICKS)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.DECAYED_STONE_BRICKS));
        wallInventory(wallInventoryToString(ModBlocks.DECAYED_STONE_WALL), prefix(ModBlocks.DECAYED_STONE));
        getBuilder(registryNameToString(ModBlocks.REFINED_SOUL_METAL_BLOCK)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.REFINED_SOUL_METAL_BLOCK));
        wallInventory(wallInventoryToString(ModBlocks.SMOOTH_SOUL_STONE_WALL), prefix(ModBlocks.SMOOTH_SOUL_STONE));
        getBuilder(registryNameToString(ModBlocks.SOUL_LAVA_FLUID_BLOCK)).texture(PARTICLE, SoulCraft.prefix("block/soul_lava_still"));
        getBuilder(registryNameToString(ModBlocks.SOUL_METAL_BLOCK)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.SOUL_METAL_BLOCK));
        getBuilder(registryNameToString(ModBlocks.SOUL_STONE)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.SOUL_STONE));
        wallInventory(wallInventoryToString(ModBlocks.SOUL_STONE_BRICK_WALL), prefix(ModBlocks.SOUL_STONE_BRICKS));
        getBuilder(registryNameToString(ModBlocks.SOUL_STONE_BRICKS)).parent(CUBE_ALL).texture(ALL, prefix(ModBlocks.SOUL_STONE_BRICKS));
        wallInventory(wallInventoryToString(ModBlocks.SOUL_STONE_WALL), prefix(ModBlocks.SOUL_STONE));
    }

    private static String wallInventoryToString(WallBlock block) {
        return registryNameToString(block) + "_inventory";
    }

    static ResourceLocation prefix(Block block) {
        Objects.requireNonNull(block.getRegistryName(), "Registry name should be non-null");
        return prefix(block.getRegistryName().getPath());
    }

    static ResourceLocation prefix(String name) {
        return SoulCraft.prefix("block/" + name);
    }
}
