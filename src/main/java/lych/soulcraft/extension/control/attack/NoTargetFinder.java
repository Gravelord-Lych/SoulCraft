package lych.soulcraft.extension.control.attack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

public enum NoTargetFinder implements TargetFinder<MobEntity> {
    INSTANCE;

    @Nullable
    @Override
    public LivingEntity findTarget(MobEntity operatingMob, ServerPlayerEntity player) {
        return null;
    }
}
