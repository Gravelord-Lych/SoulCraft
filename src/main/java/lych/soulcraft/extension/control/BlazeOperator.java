package lych.soulcraft.extension.control;

import lych.soulcraft.extension.control.attack.*;
import lych.soulcraft.extension.control.movement.DefaultMovementHandler;
import lych.soulcraft.extension.control.movement.MovementHandler;
import lych.soulcraft.extension.control.rotation.DefaultRotationHandler;
import lych.soulcraft.extension.control.rotation.RotationHandler;
import lych.soulcraft.util.Telepathy;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class BlazeOperator extends MindOperator<BlazeEntity> {
    public BlazeOperator(ControllerType<BlazeEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public BlazeOperator(ControllerType<BlazeEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super BlazeEntity> initMeleeHandler() {
        return DefaultMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super BlazeEntity> initMovementHandler() {
        return DefaultMovementHandler.NORMAL;
    }

    @Override
    protected RotationHandler<? super BlazeEntity> initRotationHandler() {
        return DefaultRotationHandler.INSTANCE;
    }

    @Override
    protected TargetFinder<? super BlazeEntity> initTargetFinder() {
        return new TelepathicTargetFinder(48, Math.PI / 6, Telepathy.DEFAULT_ANGLE_WEIGHT);
    }

    @Override
    protected RightClickHandler<? super BlazeEntity> initRightClickHandler() {
        return new BlazeRightClickHandler();
    }
}
