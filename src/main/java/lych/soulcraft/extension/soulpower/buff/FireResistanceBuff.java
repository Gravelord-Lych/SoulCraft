package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum FireResistanceBuff implements DefenseBuff {
    INSTANCE;

    @Override
    public void onEntityAttackPlayer(PlayerEntity player, LivingAttackEvent event) {
        if (event.getSource().isFire()) {
            event.setCanceled(true);
        }
    }

    @Override
    public void onPlayerHurt(PlayerEntity player, LivingHurtEvent event) {}

    @Override
    public void onPlayerDamaged(PlayerEntity player, LivingDamageEvent event) {}

    @Override
    public void onPostHurt(PlayerEntity player, PostLivingHurtEvent event) {}

    @Override
    public void tick(PlayerEntity player, ServerWorld world) {
        DefenseBuff.super.tick(player, world);
        if (player.isOnFire()) {
            player.clearFire();
        }
    }
}
