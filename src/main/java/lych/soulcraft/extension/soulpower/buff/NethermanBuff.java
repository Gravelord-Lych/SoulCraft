package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum NethermanBuff implements DamageBuff {
    INSTANCE;

    //  Functions.
    private static float calculateNethermanAttackDamageMultiplier(float temperature, boolean onFire) {
        return MathHelper.clamp((1 + temperature / 5) * (onFire ? 1.25f : 1), 0.9f, 2);
    }

    @Override
    public void onPlayerAttack(PlayerEntity player, LivingAttackEvent event) {}

    @Override
    public void onLivingHurt(PlayerEntity player, LivingHurtEvent event) {
        float temperature = player.level.getBiome(player.blockPosition()).getTemperature(player.blockPosition());
        float damageMultiplier = calculateNethermanAttackDamageMultiplier(temperature, player.isOnFire());
        event.setAmount(event.getAmount() * damageMultiplier);
    }

    @Override
    public void onLivingDamage(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}
}
