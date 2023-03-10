package lych.soulcraft.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

public enum NoMeleeHandler implements MeleeHandler<MobEntity> {
    INSTANCE;

    @Override
    public void handleMeleeAttack(MobEntity operatingMob, LivingEntity target, ServerPlayerEntity player) {}
}
