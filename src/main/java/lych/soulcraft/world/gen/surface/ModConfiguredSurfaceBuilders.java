package lych.soulcraft.world.gen.surface;

import lych.soulcraft.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public final class ModConfiguredSurfaceBuilders {
    private static final SurfaceBuilderConfig CONFIG_CRIMSON_PLAINS = new SurfaceBuilderConfig(ModBlocks.CRIMSON_HYPHAL_SOIL.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
    private static final SurfaceBuilderConfig CONFIG_INNERMOST_SOUL_LAND = new SurfaceBuilderConfig(ModBlocks.REFINED_SOUL_SOIL.defaultBlockState(), ModBlocks.REFINED_SOUL_SAND.defaultBlockState(), ModBlocks.REFINED_SOUL_SAND.defaultBlockState());
    private static final SurfaceBuilderConfig CONFIG_PARCHED_DESERT = new SurfaceBuilderConfig(ModBlocks.PARCHED_SOIL.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
    private static final SurfaceBuilderConfig CONFIG_SOUL_BEACH = new SurfaceBuilderConfig(Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
    private static final SurfaceBuilderConfig CONFIG_SOUL_LAND = new SurfaceBuilderConfig(Blocks.SOUL_SOIL.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());
    private static final SurfaceBuilderConfig CONFIG_WARPED_PLAINS = new SurfaceBuilderConfig(ModBlocks.WARPED_HYPHAL_SOIL.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());

    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> CRIMSON_PLAINS = SurfaceBuilder.DEFAULT.configured(CONFIG_CRIMSON_PLAINS);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> INNERMOST_SOUL_LAND = ModSurfaceBuilders.INNERMOST_SOUL_LAND.configured(CONFIG_INNERMOST_SOUL_LAND);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> PARCHED_DESERT = SurfaceBuilder.DEFAULT.configured(CONFIG_PARCHED_DESERT);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SOUL_BEACH = SurfaceBuilder.DEFAULT.configured(CONFIG_SOUL_BEACH);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SOUL_LAND = ModSurfaceBuilders.SOUL_LAND.configured(CONFIG_SOUL_LAND);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SOUL_LAVA_OCEAN = SurfaceBuilder.DEFAULT.configured(CONFIG_SOUL_LAND);
    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> WARPED_PLAINS = SurfaceBuilder.DEFAULT.configured(CONFIG_WARPED_PLAINS);

    private ModConfiguredSurfaceBuilders() {}
}
