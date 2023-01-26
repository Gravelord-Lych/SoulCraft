package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.item.SEGemItem;
import lych.soulcraft.item.SoulBowItem;
import lych.soulcraft.util.SoulEnergies;
import lych.soulcraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.WallBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import lych.soulcraft.item.ModItems;

import java.util.Objects;

import static lych.soulcraft.data.ModDataGens.registryNameToString;

public class ItemModelDataGen extends ItemModelProvider {
    private static final ModelFile GENERATED = new UncheckedModelFile("item/generated");
    private static final ModelFile HANDHELD = new UncheckedModelFile("item/handheld");
    private static final ModelFile SPAWN_EGG = new UncheckedModelFile("item/template_spawn_egg");
    private static final String LAYER0 = "layer0";

    public ItemModelDataGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SoulCraft.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simple(registryNameToString(ModItems.DIAMOND_CHALLENGE_MEDAL), GENERATED, prefix(ModItems.DIAMOND_CHALLENGE_MEDAL));
        simple(registryNameToString(ModItems.GOLD_CHALLENGE_MEDAL), GENERATED, prefix(ModItems.GOLD_CHALLENGE_MEDAL));
        simple(registryNameToString(ModItems.IRON_CHALLENGE_MEDAL), GENERATED, prefix(ModItems.IRON_CHALLENGE_MEDAL));
        simple(registryNameToString(ModItems.MANIPULATOR), HANDHELD, prefix(ModItems.MANIPULATOR));
        simple(registryNameToString(ModItems.NETHERITE_CHALLENGE_MEDAL), GENERATED, prefix(ModItems.NETHERITE_CHALLENGE_MEDAL));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_AXE), HANDHELD, prefix(ModItems.REFINED_SOUL_METAL_AXE));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_BOOTS), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_BOOTS));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_CHESTPLATE), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_CHESTPLATE));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_HELMET), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_HELMET));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_HOE), HANDHELD, prefix(ModItems.REFINED_SOUL_METAL_HOE));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_HORSE_ARMOR), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_HORSE_ARMOR));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_INGOT), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_INGOT));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_LEGGINGS), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_LEGGINGS));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_NUGGET), GENERATED, prefix(ModItems.REFINED_SOUL_METAL_NUGGET));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_PICKAXE), HANDHELD, prefix(ModItems.REFINED_SOUL_METAL_PICKAXE));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_SHOVEL), HANDHELD, prefix(ModItems.REFINED_SOUL_METAL_SHOVEL));
        simple(registryNameToString(ModItems.REFINED_SOUL_METAL_SWORD), HANDHELD, prefix(ModItems.REFINED_SOUL_METAL_SWORD));
        simple(registryNameToString(ModItems.SOUL_ARROW), GENERATED, prefix(ModItems.SOUL_ARROW));
        simple(registryNameToString(ModItems.SOUL_BLAZE_POWDER), GENERATED, prefix(ModItems.SOUL_BLAZE_POWDER));
        simple(registryNameToString(ModItems.SOUL_BLAZE_ROD), HANDHELD, prefix(ModItems.SOUL_BLAZE_ROD));
        getBuilder(registryNameToString(ModItems.SOUL_BOW)).parent(new UncheckedModelFile(new ResourceLocation("item/" + Objects.requireNonNull(Items.BOW.getRegistryName()).getPath()))).texture(LAYER0, prefix(ModItems.SOUL_BOW)).override()
                .predicate(SoulBowItem.PULLING, 1).model(new UncheckedModelFile(prefix(ModItems.SOUL_BOW, "_pulling_0"))).end().override()
                .predicate(SoulBowItem.PULLING, 1).predicate(SoulBowItem.PULL, 0.65f).model(new UncheckedModelFile(prefix(ModItems.SOUL_BOW, "_pulling_1"))).end().override()
                .predicate(SoulBowItem.PULLING, 1).predicate(SoulBowItem.PULL, 0.9f).model(new UncheckedModelFile(prefix(ModItems.SOUL_BOW, "_pulling_2"))).end();
        simple(registryNameToString(ModItems.SOUL_BOW) + "_pulling_0", new UncheckedModelFile(prefix(ModItems.SOUL_BOW)), prefix(ModItems.SOUL_BOW, "_pulling_0"));
        simple(registryNameToString(ModItems.SOUL_BOW) + "_pulling_1", new UncheckedModelFile(prefix(ModItems.SOUL_BOW)), prefix(ModItems.SOUL_BOW, "_pulling_1"));
        simple(registryNameToString(ModItems.SOUL_BOW) + "_pulling_2", new UncheckedModelFile(prefix(ModItems.SOUL_BOW)), prefix(ModItems.SOUL_BOW, "_pulling_2"));
        simple(registryNameToString(ModItems.SOUL_CONTAINER), GENERATED, prefix(ModItems.SOUL_CONTAINER));
        SEGem(ModItems.SOUL_ENERGY_GEM);
        SEGem(ModItems.SOUL_ENERGY_GEM_II);
        simple(registryNameToString(ModItems.SOUL_LAVA_BUCKET), GENERATED, prefix(ModItems.SOUL_LAVA_BUCKET));
        simple(registryNameToString(ModItems.SOUL_METAL_INGOT), GENERATED, prefix(ModItems.SOUL_METAL_INGOT));
        simple(registryNameToString(ModItems.SOUL_METAL_NUGGET), GENERATED, prefix(ModItems.SOUL_METAL_NUGGET));
        simple(registryNameToString(ModItems.SOUL_METAL_PARTICLE), GENERATED, prefix(ModItems.SOUL_METAL_PARTICLE));
        simple(registryNameToString(ModItems.SOUL_PIECE), GENERATED, prefix(ModItems.SOUL_PIECE));
        simple(registryNameToString(ModItems.SOUL_POWDER), GENERATED, prefix(ModItems.SOUL_POWDER));
        registerBlockItemModels();
        registerSpawnEggModels();
    }

    private void simple(String name, ModelFile model, ResourceLocation location) {
        getBuilder(name).parent(model).texture(LAYER0, location);
    }

    private void SEGem(SEGemItem item) {
        getBuilder(SEGemRegistryNameToString(item)).parent(GENERATED).texture(LAYER0, prefixSEGem(item, 0)).override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.1f).model(SEGemModel(item, 1)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.2f).model(SEGemModel(item, 2)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.3f).model(SEGemModel(item, 3)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.4f).model(SEGemModel(item, 4)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.5f).model(SEGemModel(item, 5)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.6f).model(SEGemModel(item, 6)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.7f).model(SEGemModel(item, 7)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.8f).model(SEGemModel(item, 8)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 0.9f).model(SEGemModel(item, 9)).end().override()
                .predicate(SoulEnergies.SOUL_ENERGY_LEVEL, 1).model(SEGemModel(item, 10)).end();
        simple(SEGemRegistryNameToString(item, 1), GENERATED, prefixSEGem(item, 1));
        simple(SEGemRegistryNameToString(item, 2), GENERATED, prefixSEGem(item, 2));
        simple(SEGemRegistryNameToString(item, 3), GENERATED, prefixSEGem(item, 3));
        simple(SEGemRegistryNameToString(item, 4), GENERATED, prefixSEGem(item, 4));
        simple(SEGemRegistryNameToString(item, 5), GENERATED, prefixSEGem(item, 5));
        simple(SEGemRegistryNameToString(item, 6), GENERATED, prefixSEGem(item, 6));
        simple(SEGemRegistryNameToString(item, 7), GENERATED, prefixSEGem(item, 7));
        simple(SEGemRegistryNameToString(item, 8), GENERATED, prefixSEGem(item, 8));
        simple(SEGemRegistryNameToString(item, 9), GENERATED, prefixSEGem(item, 9));
        simple(SEGemRegistryNameToString(item, 10), GENERATED, prefixSEGem(item, 10));
    }

    private void registerBlockItemModels() {
        blockItem(ModItems.CHISELED_SOUL_STONE_BRICKS);
        blockItem(ModItems.CRACKED_DECAYED_STONE_BRICK_SLAB);
        blockItem(ModItems.CRACKED_DECAYED_STONE_BRICK_STAIRS);
        blockItem(ModItems.CRACKED_DECAYED_STONE_BRICK_WALL);
        blockItem(ModItems.CRACKED_DECAYED_STONE_BRICKS);
        blockItem(ModItems.CRACKED_SOUL_STONE_BRICK_SLAB);
        blockItem(ModItems.CRACKED_SOUL_STONE_BRICK_STAIRS);
        blockItem(ModItems.CRACKED_SOUL_STONE_BRICK_WALL);
        blockItem(ModItems.CRACKED_SOUL_STONE_BRICKS);
        blockItem(ModItems.CRIMSON_HYPHAL_SOIL);
        blockItem(ModItems.DECAYED_STONE);
        blockItem(ModItems.DECAYED_STONE_BRICK_SLAB);
        blockItem(ModItems.DECAYED_STONE_BRICK_STAIRS);
        blockItem(ModItems.DECAYED_STONE_BRICK_WALL);
        blockItem(ModItems.DECAYED_STONE_BRICKS);
        blockItem(ModItems.DECAYED_STONE_SLAB);
        blockItem(ModItems.DECAYED_STONE_STAIRS);
        blockItem(ModItems.DECAYED_STONE_WALL);
        segenBlockItem(ModItems.DEPTH_SEGEN);
        segenBlockItem(ModItems.DEPTH_SEGEN_II);
        segenBlockItem(ModItems.HEAT_SEGEN);
        segenBlockItem(ModItems.HEAT_SEGEN_II);
        segenBlockItem(ModItems.NETHER_SEGEN);
        segenBlockItem(ModItems.NETHER_SEGEN_II);
        blockItem(ModItems.PARCHED_SOIL);
        blockItem(ModItems.REFINED_SOUL_METAL_BLOCK);
        blockItem(ModItems.REFINED_SOUL_SAND);
        blockItem(ModItems.REFINED_SOUL_SOIL);
        segenBlockItem(ModItems.SEGEN);
        segenBlockItem(ModItems.SEGEN_II);
        segenBlockItem(ModItems.SKY_SEGEN);
        segenBlockItem(ModItems.SKY_SEGEN_II);
        blockItem(ModItems.SMOOTH_SOUL_STONE);
        blockItem(ModItems.SMOOTH_SOUL_STONE_SLAB);
        blockItem(ModItems.SMOOTH_SOUL_STONE_STAIRS);
        blockItem(ModItems.SMOOTH_SOUL_STONE_WALL);
        segenBlockItem(ModItems.SOLAR_SEGEN);
        segenBlockItem(ModItems.SOLAR_SEGEN_II);
        blockItem(registryNameToString(ModItems.SOUL_ENERGY_STORAGE), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(ModItems.SOUL_ENERGY_STORAGE.getBlock()).getPath() + "_0")));
        blockItem(registryNameToString(ModItems.SOUL_ENERGY_STORAGE_II), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(ModItems.SOUL_ENERGY_STORAGE_II.getBlock()).getPath() + "_0")));
        blockItem(ModItems.SOUL_METAL_BLOCK);
        blockItem(ModItems.SOUL_REINFORCEMENT_TABLE);
        blockItem(ModItems.SOUL_STONE);
        blockItem(ModItems.SOUL_STONE_BRICK_SLAB);
        blockItem(ModItems.SOUL_STONE_BRICK_STAIRS);
        blockItem(ModItems.SOUL_STONE_BRICK_WALL);
        blockItem(ModItems.SOUL_STONE_BRICKS);
        blockItem(ModItems.SOUL_STONE_SLAB);
        blockItem(ModItems.SOUL_STONE_STAIRS);
        blockItem(ModItems.SOUL_STONE_WALL);
        simple(registryNameToString(ModItems.SOUL_WART), GENERATED, prefix(ModItems.SOUL_WART));
        blockItem(ModItems.WARPED_HYPHAL_SOIL);
    }

    private void registerSpawnEggModels() {
        spawnEgg(ModItems.DARK_EVOKER_SPAWN_EGG);
        spawnEgg(ModItems.ENGINEER_SPAWN_EGG);
        spawnEgg(ModItems.ILLUSORY_HORSE_SPAWN_EGG);
        spawnEgg(ModItems.REDSTONE_MORTAR_SPAWN_EGG);
        spawnEgg(ModItems.REDSTONE_TURRET_SPAWN_EGG);
        spawnEgg(ModItems.SOUL_SKELETON_SPAWN_EGG);
        spawnEgg(ModItems.VOID_ALCHEMIST_SPAWN_EGG);
        spawnEgg(ModItems.VOID_ARCHER_SPAWN_EGG);
        spawnEgg(ModItems.VOID_DEFENDER_SPAWN_EGG);
        spawnEgg(ModItems.VOIDWALKER_SPAWN_EGG);
        spawnEgg(ModItems.WANDERER_SPAWN_EGG);
    }

    private void spawnEgg(Item item) {
        getBuilder(registryNameToString(item)).parent(SPAWN_EGG);
    }

    private void segenBlockItem(BlockItem segen) {
        blockItem(registryNameToString(segen), new UncheckedModelFile(BlockModelDataGen.prefix(Utils.getRegistryName(segen.getBlock()).getPath())));
    }

    private void blockItem(BlockItem item) {
        blockItem(registryNameToString(item), item.getBlock() instanceof WallBlock ? byWall((WallBlock) item.getBlock()) : byBlock(item.getBlock()));
    }

    private void blockItem(String itemPath, ModelFile blockModel) {
        getBuilder(itemPath).parent(blockModel);
    }

    static ResourceLocation prefix(Item item) {
        return prefix(item, "");
    }

    private static ResourceLocation prefix(Item item, String ex) {
        Objects.requireNonNull(item.getRegistryName(), "Registry name should be non-null");
        return SoulCraft.prefix("item/" + item.getRegistryName().getPath() + ex);
    }

    private static ResourceLocation prefixSEGem(SEGemItem item, int level) {
        Objects.requireNonNull(item.getRegistryName(), "Registry name should be non-null");
        return SoulCraft.prefix("item/" + item.getRegistryName().getPath() + "_" + level);
    }

    private static String SEGemRegistryNameToString(SEGemItem item) {
        return registryNameToString(item);
    }

    private static String SEGemRegistryNameToString(SEGemItem item, int level) {
        return registryNameToString(item) + "_" + level;
    }

    private static ModelFile SEGemModel(SEGemItem item, int level) {
        return new UncheckedModelFile(prefixSEGem(item, level));
    }

    private ModelFile byBlock(Block block) {
        return new UncheckedModelFile(BlockModelDataGen.prefix(block));
    }

    private ModelFile byWall(WallBlock block) {
        return new UncheckedModelFile(BlockModelDataGen.prefix(block) + "_inventory");
    }
}
