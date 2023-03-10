package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.block.ModBlockStateProperties;
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

import static lych.soulcraft.block.ModBlocks.*;
import static lych.soulcraft.data.BlockModelDataGen.side;
import static lych.soulcraft.data.BlockModelDataGen.top;
import static net.minecraftforge.client.model.generators.ModelProvider.BLOCK_FOLDER;

public class BlockStateDataGen extends BlockStateProvider {
    public BlockStateDataGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SoulCraft.MOD_ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        simpleBlock(SOUL_REINFORCEMENT_TABLE, models().orientableWithBottom(name(SOUL_REINFORCEMENT_TABLE),
                side(SOUL_REINFORCEMENT_TABLE),
                side(SOUL_REINFORCEMENT_TABLE),
                BlockModelDataGen.prefix(SOUL_STONE),
                top(SOUL_REINFORCEMENT_TABLE)));
        simpleBlock(CHISELED_SOUL_STONE_BRICKS, modelFromBlock(CHISELED_SOUL_STONE_BRICKS));
        simpleBlock(CRACKED_DECAYED_STONE_BRICKS);
        slabBlock(CRACKED_DECAYED_STONE_BRICK_SLAB, BlockModelDataGen.prefix(CRACKED_DECAYED_STONE_BRICKS), BlockModelDataGen.prefix(CRACKED_DECAYED_STONE_BRICKS));
        stairsBlock(CRACKED_DECAYED_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(CRACKED_DECAYED_STONE_BRICKS));
        wallBlock(CRACKED_DECAYED_STONE_BRICK_WALL, BlockModelDataGen.prefix(CRACKED_DECAYED_STONE_BRICKS));
        simpleBlock(CRACKED_SOUL_STONE_BRICKS);
        slabBlock(CRACKED_SOUL_STONE_BRICK_SLAB, BlockModelDataGen.prefix(CRACKED_SOUL_STONE_BRICKS), BlockModelDataGen.prefix(CRACKED_SOUL_STONE_BRICKS));
        stairsBlock(CRACKED_SOUL_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(CRACKED_SOUL_STONE_BRICKS));
        wallBlock(CRACKED_SOUL_STONE_BRICK_WALL, BlockModelDataGen.prefix(CRACKED_SOUL_STONE_BRICKS));
        simpleBlock(CRIMSON_HYPHAL_SOIL, modelFromBlock(CRIMSON_HYPHAL_SOIL));
        simpleBlock(DECAYED_STONE);
        slabBlock(DECAYED_STONE_BRICK_SLAB, BlockModelDataGen.prefix(DECAYED_STONE_BRICKS), BlockModelDataGen.prefix(DECAYED_STONE_BRICKS));
        stairsBlock(DECAYED_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(DECAYED_STONE_BRICKS));
        wallBlock(DECAYED_STONE_BRICK_WALL, BlockModelDataGen.prefix(DECAYED_STONE_BRICKS));
        simpleBlock(DECAYED_STONE_BRICKS);
        slabBlock(DECAYED_STONE_SLAB, BlockModelDataGen.prefix(DECAYED_STONE), BlockModelDataGen.prefix(DECAYED_STONE));
        stairsBlock(DECAYED_STONE_STAIRS, BlockModelDataGen.prefix(DECAYED_STONE));
        wallBlock(DECAYED_STONE_WALL, BlockModelDataGen.prefix(DECAYED_STONE));
        fire(INFERNO);
        simpleBlock(PARCHED_SOIL);
        fire(POISONOUS_FIRE);
        simpleBlock(POTTED_SOULIFIED_BUSH, modelFromBlock(POTTED_SOULIFIED_BUSH));
        fire(PURE_SOUL_FIRE);
        simpleBlock(REFINED_SOUL_METAL_BLOCK);
        simpleBlock(REFINED_SOUL_SAND);
        simpleBlock(REFINED_SOUL_SOIL);
        simpleBlock(SMOOTH_SOUL_STONE);
        slabBlock(SMOOTH_SOUL_STONE_SLAB, BlockModelDataGen.prefix(SMOOTH_SOUL_STONE), BlockModelDataGen.prefix(SMOOTH_SOUL_STONE));
        stairsBlock(SMOOTH_SOUL_STONE_STAIRS, BlockModelDataGen.prefix(SMOOTH_SOUL_STONE));
        wallBlock(SMOOTH_SOUL_STONE_WALL, BlockModelDataGen.prefix(SMOOTH_SOUL_STONE));
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(SOUL_ENERGY_STORAGE)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", BlockModelDataGen.prefix(SOUL_ENERGY_STORAGE), i),
                            BlockModelDataGen.prefix(String.format("%s_%d", name(SOUL_ENERGY_STORAGE), i)),
                            BlockModelDataGen.prefix(BlockModelDataGen.SIMPLE_MACHINE_SIDE))));
        }
        for (int i = 0; i <= SEStorageTileEntity.MAX_SOUL_ENERGY_LEVEL; i++) {
            getVariantBuilder(SOUL_ENERGY_STORAGE_II)
                    .partialState()
                    .with(ModBlockStateProperties.SOUL_ENERGY_LEVEL, i)
                    .addModels(new ConfiguredModel(models().cubeColumn(
                            String.format("%s_%d", BlockModelDataGen.prefix(SOUL_ENERGY_STORAGE_II), i),
                            BlockModelDataGen.prefix(String.format("%s_%d", name(SOUL_ENERGY_STORAGE_II), i)),
                            BlockModelDataGen.prefix(BlockModelDataGen.SIMPLE_L2_MACHINE_SIDE))));
        }
        segens();
        simpleBlock(SOUL_LAVA_FLUID_BLOCK, modelFromBlock(SOUL_LAVA_FLUID_BLOCK));
        simpleBlock(SOUL_METAL_BLOCK);
        simpleBlock(SOUL_STONE);
        slabBlock(SOUL_STONE_BRICK_SLAB, BlockModelDataGen.prefix(SOUL_STONE_BRICKS), BlockModelDataGen.prefix(SOUL_STONE_BRICKS));
        stairsBlock(SOUL_STONE_BRICK_STAIRS, BlockModelDataGen.prefix(SOUL_STONE_BRICKS));
        wallBlock(SOUL_STONE_BRICK_WALL, BlockModelDataGen.prefix(SOUL_STONE_BRICKS));
        simpleBlock(SOUL_STONE_BRICKS);
        slabBlock(SOUL_STONE_SLAB, BlockModelDataGen.prefix(SOUL_STONE), BlockModelDataGen.prefix(SOUL_STONE));
        stairsBlock(SOUL_STONE_STAIRS, BlockModelDataGen.prefix(SOUL_STONE));
        wallBlock(SOUL_STONE_WALL, BlockModelDataGen.prefix(SOUL_STONE));
        cropsBlock(SOUL_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
        simpleBlock(SOULIFIED_BUSH, modelFromBlock(SOULIFIED_BUSH));
        simpleBlock(WARPED_HYPHAL_SOIL, modelFromBlock(WARPED_HYPHAL_SOIL));
    }

    private void segens() {
        sidedSegen(DEPTH_SEGEN, "depth_segen_side");
        sidedSegen(DEPTH_SEGEN_II, "depth_segen_l2_side");
        segen(HEAT_SEGEN);
        segen(HEAT_SEGEN_II);
        segen(NETHER_SEGEN);
        segen(NETHER_SEGEN_II);
        segen(SEGEN);
        segen(SEGEN_II);
        segen(SKY_SEGEN);
        segen(SKY_SEGEN_II);
        sidedSegen(SOLAR_SEGEN, false);
        sidedSegen(SOLAR_SEGEN_II, true);
    }

    private void segen(Block segen) {
        getVariantBuilder(segen)
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, false)
                .addModels(new ConfiguredModel(models().singleTexture(BlockModelDataGen.prefix(segen).toString(), BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL_NAME, BlockModelDataGen.prefix(segen))))
                .partialState()
                .with(ModBlockStateProperties.IS_GENERATING_SE, true)
                .addModels(new ConfiguredModel(models().singleTexture(BlockModelDataGen.prefix(segen) + "_gs", BlockModelDataGen.CUBE_ALL.getLocation(), BlockModelDataGen.ALL_NAME, BlockModelDataGen.prefix(name(segen) + "_gs"))));
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
