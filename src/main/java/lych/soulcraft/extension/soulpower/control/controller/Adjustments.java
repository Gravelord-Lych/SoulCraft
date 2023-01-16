package lych.soulcraft.extension.soulpower.control.controller;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.event.RegisterControlledMobBehaviorsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static lych.soulcraft.SoulCraft.prefix;
import static lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior.register;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID)
public final class Adjustments {
    public static final Adjustment<RaiderAdjustment> RAIDERS = new RaiderAdjustment.Type(prefix("raider_adjustment"));
    public static final Adjustment<GuardianAdjustment> GUARDIANS = new GuardianAdjustment.Type(prefix("guardian_adjustment"));

    @SubscribeEvent
    public static void registerAdjustments(RegisterControlledMobBehaviorsEvent event) {
        register(RAIDERS);
        register(GUARDIANS);
    }

    private Adjustments() {}
}
