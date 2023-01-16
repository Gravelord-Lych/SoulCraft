package lych.soulcraft.effect;

import lych.soulcraft.SoulCraft;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soulcraft.SoulCraft.make;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEffects {
    public static final Effect REVERSION = new CommonEffect(EffectType.HARMFUL, 0xf800f8);
    public static final Effect CATASTROPHE_OMEN = new CatastropheOmenEffect(EffectType.NEUTRAL, 0x441818);

    private ModEffects() {}

    @SubscribeEvent
    public static void registerEffects(RegistryEvent.Register<Effect> event) {
        IForgeRegistry<Effect> registry = event.getRegistry();
        registry.register(make(REVERSION, ModEffectNames.REVERSION));
        registry.register(make(CATASTROPHE_OMEN, ModEffectNames.CATASTROPHE_OMEN));
    }
}
