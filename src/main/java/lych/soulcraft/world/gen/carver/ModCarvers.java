package lych.soulcraft.world.gen.carver;

import lych.soulcraft.SoulCraft;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModCarvers {
    public static final WorldCarver<ProbabilityConfig> SOUL_CAVE = new SoulCaveCarver(ProbabilityConfig.CODEC, 256);

    private ModCarvers() {}

    @SubscribeEvent
    public static void registerWorldCarvers(RegistryEvent.Register<WorldCarver<?>> event) {
        IForgeRegistry<WorldCarver<?>> registry = event.getRegistry();
        registry.register(SoulCraft.make(SOUL_CAVE, ModCarverNames.SOUL_CAVE));
    }
}
