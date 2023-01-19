package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum PoisonerBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {
        event.getEntityLiving().addEffect(new EffectInstance(ExtraAbilityConstants.POISONER_POISON_EFFECT));
    }

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
