package lych.soulcraft.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectType;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;

public class CatastropheOmenEffect extends CommonEffect {
    public CatastropheOmenEffect(EffectType category, int color) {
        super(category, color);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof ServerPlayerEntity && !entity.isSpectator()) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            ServerWorld world = player.getLevel();
            if (world.getDifficulty() == Difficulty.PEACEFUL) {
                return;
            }
            if (world.isVillage(entity.blockPosition())) {
                world.getRaids().createOrExtendRaid(player);
            }
        }
    }
}
