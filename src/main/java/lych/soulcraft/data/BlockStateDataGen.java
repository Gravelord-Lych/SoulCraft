package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.block.ModBlockStateProperties;
import lych.soulcraft.block.ModBlocks;
import lych.soulcraft.block.entity.SEStorageTileEntity;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static lych.soulcraft.data.BlockModelDataGen.side;
import static lych.soulcraft.data.BlockModelDataGen.top;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SoulCraft.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(ModBlocks.SOUL_REINFORCEMENT_TABLE, models().orientableWithBottom(name(ModBlocks.SOUL_REINFORCEMENT_TABLE),
                side(ModBlocks.SOUL_REINFORCEMENT_TABLE),
                side(ModBlocks.SOUL_REINFORCEMENT_TABLE),
                BlockModelDataGen.prefix(ModBlocks.SOUL_STONE),
                top(ModBlocks.SOUL_REINFORCEMENT_TABLE)));
        simpleBlock(ModBlocks.CHISELED_SOUL_STONE_BRICKS, modelFromBlock(ModBlocks.CHISELED_SOUL_STONE_BRICKS));
        simpleBlock(ModBlocks.CRACKED_DECAYED_STONE_BRICKS);
        slabBlock(ModBlocks.CRACKED_DECAYED_STONE_BRICK_SLAB, BlockModelDataGen.prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS), BlockModelDataGen.prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS));
        stairsBlock(ModBlocks.CRACKED_DECAYED_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS));
        wallBlock(ModBlocks.CRACKED_DECAYED_STONE_BRICK_WALL, BlockModelDataGen.prefix(ModBlocks.CRACKED_DECAYED_STONE_BRICKS));
        simpleBlock(ModBlocks.CRACKED_SOUL_STONE_BRICKS);
        slabBlock(ModBlocks.CRACKED_SOUL_STONE_BRICK_SLAB, BlockModelDataGen.prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS), BlockModelDataGen.prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS));
        stairsBlock(ModBlocks.CRACKED_SOUL_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS));
        wallBlock(ModBlocks.CRACKED_SOUL_STONE_BRICK_WALL, BlockModelDataGen.prefix(ModBlocks.CRACKED_SOUL_STONE_BRICKS));
        simpleBlock(ModBlocks.CRIMSON_HYPHAL_SOIL, modelFromBlock(ModBlocks.CRIMSON_HYPHAL_SOIL));
        simpleBlock(ModBlocks.DECAYED_STONE);
        slabBlock(ModBlocks.DECAYED_STONE_BRICK_SLAB, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE_BRICKS), BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE_BRICKS));
        stairsBlock(ModBlocks.DECAYED_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE_BRICKS));
        wallBlock(ModBlocks.DECAYED_STONE_BRICK_WALL, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE_BRICKS));
        simpleBlock(ModBlocks.DECAYED_STONE_BRICKS);
        slabBlock(ModBlocks.DECAYED_STONE_SLAB, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE), BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE));
        stairsBlock(ModBlocks.DECAYED_STONE_STAIRS, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE));
        wallBlock(ModBlocks.DECAYED_STONE_WALL, BlockModelDataGen.prefix(ModBlocks.DECAYED_STONE));
        fire(ModBlocks.INFERNO);
        simpleBlock(ModBlocks.PARCHED_SOIL);
        fire(ModBlocks.POISONOUS_FIRE);
        fire(ModBlocks.PURE_SOUL_FIRE);
        simpleBlock(ModBlocks.REFINED_SOUL_METAL_BLOCK);
        simpleBlock(ModBlocks.REFINED_SOUL_SAND);
        simpleBlock(ModBlocks.REFINED_SOUL_SOIL);
        simpleBlock(ModBlocks.SMOOTH_SOUL_STONE);
        slabBlock(ModBlocks.SMOOTH_SOUL_STONE_SLAB, BlockModelDataGen.prefix(ModBlocks.SMOOTH_SOUL_STONE), BlockModelDataGen.prefix(ModBlocks.SMOOTH_SOUL_STONE));
        stairsBlock(ModBlocks.SMOOTH_SOUL_STONE_STAIRS, BlockModelDataGen.prefix(ModBlocks.SMOOTH_SOUL_STONE));
        wallBlock(ModBlocks.SMOOTH_SOUL_STONE_WALL, BlockModelDataGen.prefix(ModBlocks.SMOOTH_SOUL_STONE));
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(ModBlocks.SOUL_ENERGY_STORAGE)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", BlockModelDataGen.prefix(ModBlocks.SOUL_ENERGY_STORAGE), i),
                            BlockModelDataGen.prefix(String.format("%s_%d", name(ModBlocks.SOUL_ENERGY_STORAGE), i)),
                            BlockModelDataGen.prefix(BlockModelDataGen.SIMPLE_MACHINE_SIDE))));
        }
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(ModBlocks.SOUL_ENERGY_STORAGE_II)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", BlockModelDataGen.prefix(ModBlocks.SOUL_ENERGY_STORAGE_II), i),
                            BlockModelDataGen.prefix(String.format("%s_%d", name(ModBlocks.SOUL_ENERGY_STORAGE_II), i)),
                            BlockModelDataGen.prefix(BlockModelDataGen.SIMPLE_L2_MACHINE_SIDE))));
        }
        segens();
        simpleBlock(ModBlocks.SOUL_LAVA_FLUID_BLOCK, modelFromBlock(ModBlocks.SOUL_LAVA_FLUID_BLOCK));
        simpleBlock(ModBlocks.SOUL_METAL_BLOCK);
        simpleBlock(ModBlocks.SOUL_STONE);
        slabBlock(ModBlocks.SOUL_STONE_BRICK_SLAB, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE_BRICKS), BlockModelDataGen.prefix(ModBlocks.SOUL_STONE_BRICKS));
        stairsBlock(ModBlocks.SOUL_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE_BRICKS));
        wallBlock(ModBlocks.SOUL_STONE_BRICK_WALL, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE_BRICKS));
        simpleBlock(ModBlocks.SOUL_STONE_BRICKS);
        slabBlock(ModBlocks.SOUL_STONE_SLAB, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE), BlockModelDataGen.prefix(ModBlocks.SOUL_STONE));
        stairsBlock(ModBlocks.SOUL_STONE_STAIRS, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE));
        wallBlock(ModBlocks.SOUL_STONE_WALL, BlockModelDataGen.prefix(ModBlocks.SOUL_STONE));
        cropsBlock(ModBlocks.SOUL_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
        simpleBlock(ModBlocks.WARPED_HYPHAL_SOIL, modelFromBlock(ModBlocks.WARPED_HYPHAL_SOIL));
    }

    private void segens() {
        sidedSegen(ModBlocks.DEPTH_SEGEN, "depth_segen_side");
        sidedSegen(ModBlocks.DEPTH_SEGEN_II, "depth_segen_l2_side");
        segen(ModBlocks.HEAT_SEGEN);
        segen(ModBlocks.HEAT_SEGEN_II);
        segen(ModBlocks.NETHER_SEGEN);
        segen(ModBlocks.NETHER_SEGEN_II);
        segen(ModBlocks.SEGEN);
        segen(ModBlocks.SEGEN_II);
        segen(ModBlocks.SKY_SEGEN);
        segen(ModBlocks.SKY_SEGEN_II);
        sidedSegen(ModBlocks.SOLAR_SEGEN, false);
        sidedSegen(ModBlocks.SOLAR_SEGEN_II, true);
    }

    private void segen(Block segen) {
        getVariantBuilder(segen)
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, false)
                .addModels(new ConfiguredModel(models().singleTexture(BlockModelDataGen.prefix(segen).toString(), BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL, BlockModelDataGen.prefix(segen))))
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, true)
                .addModels(new ConfiguredModel(models().singleTexture(BlockModelDataGen.prefix(segen) + "_gs", BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL, BlockModelDataGen.prefix(name(segen) + "_gs"))));
    }

    private void sidedSegen(Block segen, boolean l2) {
        sidedSegen(segen, l2 ? BlockModelDataGen.SIMPLE_L2_MACHINE_SIDE : BlockModelDataGen.SIMPLE_MACHINE_SIDE);
    }

    private void sidedSegen(Block segen, String sideTex) {
        getVariantBuilder(segen)
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, false)
                .addModels(new ConfiguredModel(models().orientable(BlockModelDataGen.prefix(segen).toString(),
                        BlockModelDataGen.prefix(sideTex),
                        BlockModelDataGen.prefix(sideTex),
                        BlockModelDataGen.prefix(segen))))
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, true)
                .addModels(new ConfiguredModel(models().orientable(BlockModelDataGen.prefix(segen) + "_gs",
                        BlockModelDataGen.prefix(sideTex),
                        BlockModelDataGen.prefix(sideTex),
                        BlockModelDataGen.prefix(name(segen) + "_gs"))));
    }

    protected ConfiguredModel modelFromBlock(Block block) {
        return new ConfiguredModel(new UncheckedModelFile(blockTexture(block)));
    }

    protected void rotatedBlock(Block block) {
        simpleBlock(block, new ConfiguredModel(cubeAll(block)), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(90).buildLast(), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(180).buildLast(), ConfiguredModel.builder().modelFile(cubeAll(block)).rotationY(270).buildLast());
    }

    protected void cropsBlock(Block block, IntegerProperty ageProperty, int... serialNumbers) {
        List<Integer> possibleValues = ageProperty.getPossibleValues().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
        if (possibleValues.size() != serialNumbers.length) {
            throw new IllegalArgumentException(String.format("Illegal serialNumbers length, length required: %d, provided: %d", possibleValues.size(), serialNumbers.length));
        }
        for (int i = 0; i < possibleValues.size(); i++) {
            int value = possibleValues.get(i);
            getVariantBuilder(block).partialState().with(ageProperty, value).addModels(new ConfiguredModel(models().crop(String.format("block/%s_stage%d", name(block), serialNumbers[i]), SoulCraft.prefix(String.format("block/%s_stage%d", name(block), serialNumbers[i])))));
        }
    }

    protected void fire(Block block) {
        fire(block,
                fireFloorModel(block, 0),
                fireFloorModel(block, 1),
                fireSideModel(block, 0, false),
                fireSideModel(block, 1, false),
                fireSideModel(block, 0, true),
                fireSideModel(block, 1, true));
    }

    protected void fire(Block block, ModelFile floor0, ModelFile floor1, ModelFile side0, ModelFile side1, ModelFile sideAlt0, ModelFile sideAlt1) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);
//      Floor
        builder.part()
                .modelFile(floor0)
                .nextModel()
                .modelFile(floor1)
                .addModel();
//      North
        builder.part()
                .modelFile(side0)
                .nextModel()
                .modelFile(side1)
                .nextModel()
                .modelFile(sideAlt0)
                .nextModel()
                .modelFile(sideAlt1)
                .addModel();
//      East
        builder.part()
                .modelFile(side0)
                .rotationY(90)
                .nextModel()
                .modelFile(side1)
                .rotationY(90)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(90)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(90)
                .addModel()
                .useOr();
//      South
        builder.part()
                .modelFile(side0)
                .rotationY(180)
                .nextModel()
                .modelFile(side1)
                .rotationY(180)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(180)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(180)
                .addModel();
//      West
        builder.part()
                .modelFile(side0)
                .rotationY(270)
                .nextModel()
                .modelFile(side1)
                .rotationY(270)
                .nextModel()
                .modelFile(sideAlt0)
                .rotationY(270)
                .nextModel()
                .modelFile(sideAlt1)
                .rotationY(270)
                .addModel();
    }

    private BlockModelBuilder fireFloorModel(Block fireBlock, int texId) {
        return models().withExistingParent(name(fireBlock) + "_floor" + texId, BLOCK_FOLDER + "/template_fire_floor").texture("fire", BlockModelDataGen.prefix(fireBlock) + "_" + texId);
    }

    private BlockModelBuilder fireSideModel(Block fireBlock, int texId, boolean alt) {
        return models().withExistingParent(name(fireBlock) + "_side" + (alt ? "_alt" : "") + texId, BLOCK_FOLDER + "/template_fire_side" + (alt ? "_alt" : "")).texture("fire", BlockModelDataGen.prefix(fireBlock) + "_" + texId);
    }

    private BlockModelBuilder fireUpModel(Block fireBlock, int texId, boolean alt) {
        return models().withExistingParent(name(fireBlock) + "_up" + (alt ? "_alt" : "") + texId, BLOCK_FOLDER + "/template_fire_up" + (alt ? "_alt" : "")).texture("fire", BlockModelDataGen.prefix(fireBlock) + "_" + texId);
    }

    private static String name(Block block) {
        return Objects.requireNonNull(block.getRegistryName(), "Registry name should be non-null").getPath();
    }
}
