package lych.soulcraft.world.gen.feature;

import lych.soulcraft.SoulCraft;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soulcraft.SoulCraft.make;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModFeatures {
    public static final Feature<NoFeatureConfig> SL_TWISTING_VINE = new SLTwistingVineFeature(NoFeatureConfig.CODEC);

    @SubscribeEvent
    public static void registerFeatures(RegistryEvent.Register<Feature<?>> event) {
        IForgeRegistry<Feature<?>> registry = event.getRegistry();
        registry.register(make(SL_TWISTING_VINE, "soul_land_twisting_vine"));
    }

    private ModFeatures() {}
}
