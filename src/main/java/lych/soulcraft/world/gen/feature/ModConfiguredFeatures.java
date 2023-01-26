package lych.soulcraft.world.gen.feature;

import com.google.common.collect.ImmutableSet;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.block.ModBlocks;
import lych.soulcraft.extension.fire.Fire;
import lych.soulcraft.extension.fire.Fires;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.blockplacer.SimpleBlockPlacer;
import net.minecraft.world.gen.blockstateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.function.UnaryOperator;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModConfiguredFeatures {
    private static final HugeFungusConfig CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(ModBlocks.CRIMSON_HYPHAL_SOIL.defaultBlockState(), Blocks.CRIMSON_STEM.defaultBlockState(), Blocks.NETHER_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false);
    private static final HugeFungusConfig WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG = new HugeFungusConfig(ModBlocks.WARPED_HYPHAL_SOIL.defaultBlockState(), Blocks.WARPED_STEM.defaultBlockState(), Blocks.WARPED_WART_BLOCK.defaultBlockState(), Blocks.SHROOMLIGHT.defaultBlockState(), false);
    public static final ConfiguredFeature<?, ?> PATCH_INFERNO = firePatch(Fires.INFERNO, ModBlocks.PARCHED_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_POISONOUS_FIRE = firePatch(Fires.POISONOUS_FIRE, ModBlocks.WARPED_HYPHAL_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_PURE_SOUL_FIRE = firePatch(Fires.PURE_SOUL_FIRE, ModBlocks.REFINED_SOUL_SAND, ModBlocks.REFINED_SOUL_SOIL);
    public static final ConfiguredFeature<?, ?> PATCH_SOUL_WART = Feature.RANDOM_PATCH.configured(new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(ModBlocks.SOUL_WART.defaultBlockState()), new SimpleBlockPlacer()).tries(96).xspread(12).zspread(12).noProjection().build()).range(128);
    public static final ConfiguredFeature<?, ?> SL_CRIMSON_FUNGI = Feature.HUGE_FUNGUS.configured(CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4)));
    public static final ConfiguredFeature<?, ?> SL_CRIMSON_FUNGI_AT_THE_EDGE = Feature.HUGE_FUNGUS.configured(CRIMSON_PLAINS_CRIMSON_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2)));
    public static final ConfiguredFeature<?, ?> SL_PATCH_SOUL_FIRE = firePatch(Fires.SOUL_FIRE, b -> b.xspread(9).yspread(4).zspread(9).tries(96), Blocks.SOUL_SAND, Blocks.SOUL_SOIL);
    public static final ConfiguredFeature<?, ?> SL_TWISTING_VINE = ModFeatures.SL_TWISTING_VINE.configured(NoFeatureConfig.INSTANCE);
    public static final ConfiguredFeature<?, ?> SL_WARPED_FUNGI = Feature.HUGE_FUNGUS.configured(WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(4)));
    public static final ConfiguredFeature<?, ?> SL_WARPED_FUNGI_AT_THE_EDGE = Feature.HUGE_FUNGUS.configured(WARPED_PLAINS_WARPED_FUNGI_NOT_PLANTED_CONFIG).decorated(Placement.COUNT_MULTILAYER.configured(new FeatureSpreadConfig(2)));
    public static final ConfiguredFeature<?, ?> SL_WEEPING_VINE = ModFeatures.SL_WEEPING_WINE.configured(NoFeatureConfig.INSTANCE);

    private ModConfiguredFeatures() {}

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        register("patch_inferno", PATCH_INFERNO);
        register("patch_poisonous_fire", PATCH_POISONOUS_FIRE);
        register("patch_pure_soul_fire", PATCH_PURE_SOUL_FIRE);
        register("patch_soul_wart", PATCH_SOUL_WART);
        register("sl_crimson_fungi", SL_CRIMSON_FUNGI);
        register("sl_crimson_fungi_at_the_edge", SL_CRIMSON_FUNGI_AT_THE_EDGE);
        register("sl_patch_soul_fire", SL_PATCH_SOUL_FIRE);
        register("sl_twisting_vine", SL_TWISTING_VINE);
        register("sl_warped_fungi", SL_WARPED_FUNGI);
        register("sl_warped_fungi_at_the_edge", SL_WARPED_FUNGI_AT_THE_EDGE);
        register("sl_weeping_vine", SL_WEEPING_VINE);
    }

    private static ConfiguredFeature<?, ?> firePatch(Fire fire, Block... whiteList) {
        return firePatch(fire, UnaryOperator.identity(), whiteList);
    }

    private static ConfiguredFeature<?, ?> firePatch(Fire fire, UnaryOperator<BlockClusterFeatureConfig.Builder> operator, Block... whiteList) {
        return firePatch(fire.getBlock().defaultBlockState(), operator, whiteList);
    }

    private static ConfiguredFeature<?, ?> firePatch(BlockState fireBlock, UnaryOperator<BlockClusterFeatureConfig.Builder> operator, Block... whiteList) {
        BlockClusterFeatureConfig.Builder builder = new BlockClusterFeatureConfig.Builder(new SimpleBlockStateProvider(fireBlock), new SimpleBlockPlacer());
        return Feature.RANDOM_PATCH.configured(operator.apply(builder).whitelist(ImmutableSet.copyOf(whiteList)).noProjection().build()).decorated(Features.Placements.FIRE);
    }

    private static <FC extends IFeatureConfig> void register(String name, ConfiguredFeature<FC, ?> feature) {
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, SoulCraft.prefix(name), feature);
    }
}
