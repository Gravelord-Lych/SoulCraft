package lych.soulcraft.extension.control;

import lych.soulcraft.extension.control.attack.*;
import lych.soulcraft.extension.control.movement.MovementHandler;
import lych.soulcraft.extension.control.rotation.RotationHandler;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.highlight.HighlighterType;
import lych.soulcraft.network.MovementData;
import lych.soulcraft.util.DefaultValues;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.controller.JumpController;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public abstract class MindOperator<T extends MobEntity> extends Controller<T> {
    private static final UUID REACH_DISTANCE_PENALTY_UUID = UUID.fromString("B926A3CB-9FAB-B2F7-0D02-74F921971CFF");
    private static final AttributeModifier REACH_DISTANCE_PENALTY = new AttributeModifier(REACH_DISTANCE_PENALTY_UUID, "Mind operator reach distance penalty", -0.75, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private final MeleeHandler<? super T> meleeHandler = initMeleeHandler();
    private final MovementHandler<? super T> movementHandler = initMovementHandler();
    private final RightClickHandler<? super T> rightClickHandler = initRightClickHandler();
    private final RotationHandler<? super T> rotationHandler = initRotationHandler();
    private final TargetFinder<? super T> targetFinder = initTargetFinder();
    private JumpController specialJumpControl;
    private float rotationDelta;

    public MindOperator(ControllerType<T> type, UUID mob, UUID player, ServerWorld level) {
        super(type, mob, player, level);
    }

    public MindOperator(ControllerType<T> type, CompoundNBT compoundNBT, ServerWorld level) {
        super(type, compoundNBT, level);
        setRotationDelta(compoundNBT.getFloat("RotationDelta"));
    }

    @Override
    public final void startControlling(MobEntity mob, GoalSelector goalSelector, GoalSelector targetSelector) {
        if (getPlayer() != null) {
            goalSelector.addGoal(0, DefaultValues.dummyGoal());
            targetSelector.addGoal(0, DefaultValues.dummyGoal());
            PlayerEntity player = getPlayer();
            IPlayerEntityMixin playerM = (IPlayerEntityMixin) player;
            MobEntity operatingMob = playerM.getOperatingMob();
            if (operatingMob == mob) {
                specialJumpControl = new JumpController(mob);
                return;
            }
            if (operatingMob != null) {
                playerM.setOperatingMob(null);
                getSoulManager().removeIf(operatingMob, c -> c instanceof MindOperator);
            }
            EntityUtils.setTarget(mob, null);
            playerM.setOperatingMob(mob);
            specialJumpControl = new JumpController(mob);
            EntityUtils.addPermanentModifierIfAbsent(player, ForgeMod.REACH_DISTANCE.get(), REACH_DISTANCE_PENALTY);
        }
    }

    @Override
    public final boolean overrideBehaviorGoals() {
        return true;
    }

    @Override
    public final boolean overrideTargetGoals() {
        return true;
    }

    @Override
    public final void stopControlling(MobEntity mob, boolean mobAlive) {
        super.stopControlling(mob, mobAlive);
        if (getPlayer() != null) {
            ((IPlayerEntityMixin) getPlayer()).setOperatingMob(null);
            EntityUtils.getAttribute(getPlayer(), ForgeMod.REACH_DISTANCE.get()).removeModifier(REACH_DISTANCE_PENALTY);
        }
    }

    @Override
    public final int getPriority() {
        return Integer.MIN_VALUE;
    }

    @SuppressWarnings("unchecked")
    public void handleMeleeRaw(MobEntity operatingMob, ServerPlayerEntity player) {
        handleMelee((T) operatingMob, player);
    }

    public void handleMelee(T operatingMob, ServerPlayerEntity player) {
        LivingEntity target = targetFinder.findTarget(operatingMob, player);
        if (target != null) {
            meleeHandler.handleMeleeAttack(operatingMob, target, player);
        }
    }

    public void handleMovement(T operatingMob, ServerPlayerEntity player, MovementData movement) {
        movementHandler.handleMovement(operatingMob, player, movement, specialJumpControl);
    }

    @SuppressWarnings("unchecked")
    public void handleRightClickRaw(MobEntity operatingMob, ServerPlayerEntity player) {
        handleRightClick((T) operatingMob, player);
    }

    public void handleRightClick(T operatingMob, ServerPlayerEntity player) {
        LivingEntity target = targetFinder.findTarget(operatingMob, player);
        if (rightClickHandler.needsExactTarget() && target != null) {
            rightClickHandler.handleRightClick(operatingMob, target, player);
        } else {
            if (target != null) {
                rightClickHandler.handleRightClick(operatingMob, target, player);
            } else {
                rightClickHandler.handleRightClick(operatingMob, player);
            }
        }
    }

    public void handleRotation(T operatingMob, ServerPlayerEntity player) {
        rotationHandler.handleRotation(operatingMob, player, rotationDelta);
    }

    public void handleRotationOffset(T operatingMob, ServerPlayerEntity player, double amount) {
        setRotationDelta(getRotationDelta() + rotationHandler.handleRotationOffset(operatingMob, player, amount));
        normalizeRotationDelta();
    }

    @Override
    public void tick() {
        super.tick();
        if (getPlayer() != null) {
            getMob().ifPresent(mob -> {
                ServerPlayerEntity player = (ServerPlayerEntity) getPlayer();
                handleRotation(mob, player);
                LivingEntity target = targetFinder.findTarget(mob, player);
                if (target != null) {
                    EntityHighlightManager.get(level).highlight(HighlighterType.MIND_OPERATOR_HELPER, target);
                }
                tickHandlers(mob, player);
            });
        }
        if (specialJumpControl != null) {
            specialJumpControl.tick();
        }
        if (specialJumpControl == null && getMob().isPresent()) {
            specialJumpControl = new JumpController(getMob().get());
        }
        normalizeRotationDelta();
    }

    private void normalizeRotationDelta() {
        while (rotationDelta >= 360) {
            rotationDelta -= 360;
        }
        while (rotationDelta < 0) {
            rotationDelta += 360;
        }
    }

    private void tickHandlers(T mob, ServerPlayerEntity player) {
        meleeHandler.tick(mob, player);
        movementHandler.tick(mob, player);
        rightClickHandler.tick(mob, player);
        rotationHandler.tick(mob, player);
        targetFinder.tick(mob, player);
    }

    protected abstract MeleeHandler<? super T> initMeleeHandler();

    protected abstract MovementHandler<? super T> initMovementHandler();

    protected RightClickHandler<? super T> initRightClickHandler() {
        return NoRightClickHandler.INSTANCE;
    }

    protected abstract RotationHandler<? super T> initRotationHandler();

    protected TargetFinder<? super T> initTargetFinder() {
        return RangedTargetFinder.DEFAULT;
    }

    public float getRotationDelta() {
        return rotationDelta;
    }

    public void setRotationDelta(float rotationDelta) {
        this.rotationDelta = rotationDelta;
    }

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = super.save();
        compoundNBT.putFloat("RotationDelta", getRotationDelta());
        return compoundNBT;
    }
}
