package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcements;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum PoisonerBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {
        int caveSpiderLevel = ReinforcementHelper.getReinforcementLevel(player.getMainHandItem(), Reinforcements.CAVE_SPIDER);
        int beeLevel = ReinforcementHelper.getReinforcementLevel(player.getMainHandItem(), Reinforcements.BEE);
        event.getEntityLiving().addEffect(new EffectInstance(Effects.POISON, ExtraAbilityConstants.POISONER_POISON_EFFECT_DURATION + ExtraAbilityConstants.POISONER_ADDITIONAL_POISON_EFFECT_DURATION * (caveSpiderLevel + beeLevel)));
    }

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
