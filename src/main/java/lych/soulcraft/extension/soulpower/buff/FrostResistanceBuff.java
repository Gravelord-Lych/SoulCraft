package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum FrostResistanceBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {
        float temperature = player.level.getBiome(player.blockPosition()).getTemperature(player.blockPosition());
        float damageMultiplier = ExtraAbilityConstants.calculateFrostResistanceDamageMultiplier(temperature, player.isOnFire());
        event.setAmount(event.getAmount() * damageMultiplier);
    }

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
