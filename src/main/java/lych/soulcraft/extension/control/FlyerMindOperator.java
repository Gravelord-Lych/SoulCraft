package lych.soulcraft.extension.control;

import lych.soulcraft.extension.control.attack.MeleeHandler;
import lych.soulcraft.extension.control.attack.NoMeleeHandler;
import lych.soulcraft.extension.control.attack.NoTargetFinder;
import lych.soulcraft.extension.control.attack.TargetFinder;
import lych.soulcraft.extension.control.movement.FlyerMovementHandler;
import lych.soulcraft.extension.control.movement.MovementHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class FlyerMindOperator extends DefaultMindOperator {
    public FlyerMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public FlyerMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MovementHandler<? super MobEntity> initMovementHandler() {
        return FlyerMovementHandler.NORMAL;
    }

    @Override
    protected MeleeHandler<? super MobEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected TargetFinder<? super MobEntity> initTargetFinder() {
        return NoTargetFinder.INSTANCE;
    }
}
