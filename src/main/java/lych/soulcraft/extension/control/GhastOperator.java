package lych.soulcraft.extension.control;

import lych.soulcraft.extension.control.attack.*;
import lych.soulcraft.extension.control.movement.FlyerMovementHandler;
import lych.soulcraft.extension.control.movement.MovementHandler;
import lych.soulcraft.extension.control.rotation.DefaultRotationHandler;
import lych.soulcraft.extension.control.rotation.RotationHandler;
import lych.soulcraft.util.Telepathy;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class GhastOperator extends MindOperator<GhastEntity> {
    public GhastOperator(ControllerType<GhastEntity> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public GhastOperator(ControllerType<GhastEntity> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
    }

    @Override
    protected MeleeHandler<? super GhastEntity> initMeleeHandler() {
        return NoMeleeHandler.INSTANCE;
    }

    @Override
    protected MovementHandler<? super GhastEntity> initMovementHandler() {
        return FlyerMovementHandler.NORMAL;
    }

    @Override
    protected RotationHandler<? super GhastEntity> initRotationHandler() {
        return DefaultRotationHandler.INSTANCE;
    }

    @Override
    protected RightClickHandler<? super GhastEntity> initRightClickHandler() {
        return new GhastRightClickHandler();
    }

    @Override
    protected TargetFinder<? super GhastEntity> initTargetFinder() {
        return new TelepathicTargetFinder(60, Math.PI / 6, Telepathy.DEFAULT_ANGLE_WEIGHT);
    }
}
