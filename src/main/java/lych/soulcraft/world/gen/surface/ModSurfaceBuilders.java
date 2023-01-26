package lych.soulcraft.world.gen.surface;

import lych.soulcraft.SoulCraft;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soulcraft.SoulCraft.make;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSurfaceBuilders {
    public static final SurfaceBuilder<SurfaceBuilderConfig> INNERMOST_SOUL_LAND = new InnermostSoulLandSurfaceBuilder(SurfaceBuilderConfig.CODEC);
    public static final SurfaceBuilder<SurfaceBuilderConfig> SOUL_LAND = new SoulLandSurfaceBuilder(SurfaceBuilderConfig.CODEC);

    @SubscribeEvent
    public static void registerSurfaceBuilders(RegistryEvent.Register<SurfaceBuilder<?>> event) {
        IForgeRegistry<SurfaceBuilder<?>> registry = event.getRegistry();
        registry.register(make(INNERMOST_SOUL_LAND, suffix(ModSurfaceBuilderNames.INNERMOST_SOUL_LAND)));
        registry.register(make(SOUL_LAND, suffix(ModSurfaceBuilderNames.SOUL_LAND)));
    }

    private static String suffix(String name) {
        return name + "_surface_builder";
    }
}
