package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum NethermanBuff implements DamageBuff {
    INSTANCE;

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {
        float temperature = player.level.getBiome(player.blockPosition()).getTemperature(player.blockPosition());
        float damageMultiplier = ExtraAbilityConstants.calculateNethermanAttackDamageMultiplier(temperature, player.isOnFire());
        event.setAmount(event.getAmount() * damageMultiplier);
    }

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
