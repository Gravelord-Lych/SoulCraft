package lych.soulcraft.extension.control;

import lych.soulcraft.extension.control.attack.DefaultMeleeHandler;
import lych.soulcraft.extension.control.attack.MeleeHandler;
import lych.soulcraft.extension.control.movement.DefaultMovementHandler;
import lych.soulcraft.extension.control.movement.MovementHandler;
import lych.soulcraft.extension.control.rotation.DefaultRotationHandler;
import lych.soulcraft.extension.control.rotation.RotationHandler;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class DefaultMindOperator extends MindOperator<MobEntity> {
    public DefaultMindOperator(ControllerType<MobEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public DefaultMindOperator(ControllerType<MobEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super MobEntity> initMeleeHandler() {
        return DefaultMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super MobEntity> initMovementHandler() {
        return DefaultMovementHandler.NORMAL;
    }

    @Override
    protected RotationHandler<? super MobEntity> initRotationHandler() {
        return DefaultRotationHandler.INSTANCE;
    }
}
