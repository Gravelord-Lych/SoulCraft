package lych.soulcraft.world.gen.surface;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

//@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfiguredSurfaceBuilders {
    private static final SurfaceBuilderConfig CONFIG_SOUL_LAND = new SurfaceBuilderConfig(Blocks.SOUL_SOIL.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState(), Blocks.SOUL_SAND.defaultBlockState());

    public static final ConfiguredSurfaceBuilder<SurfaceBuilderConfig> SOUL_LAND = SurfaceBuilder.SOUL_SAND_VALLEY.configured(CONFIG_SOUL_LAND);
}
