package lych.soulcraft.extension.soulpower.control.controller;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import lych.soulcraft.entity.ai.brain.sensor.ModSensors;
import lych.soulcraft.entity.ai.brain.task.ControlledEnemyFindNewTargetTask;
import lych.soulcraft.entity.ai.brain.task.ControlledEnemyFindTargetTask;
import lych.soulcraft.entity.ai.goal.BetterHurtByTargetGoal;
import lych.soulcraft.extension.soulpower.control.BrainTaskHelper;
import lych.soulcraft.util.mixin.INearestAttackableTargetGoalMixin;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.FindNewAttackTargetTask;
import net.minecraft.entity.ai.brain.task.ForgetAttackTargetTask;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class EnemyControl extends ControlledMobBehavior.Instance {
    public EnemyControl(MobEntity mob, ServerWorld level) {
        this(DefaultControllers.DEFAULT_ENEMY_CONTROL, mob, level);
    }

    public EnemyControl(ControlledMobBehavior<?> type, MobEntity mob, ServerWorld level) {
        super(type, mob, level);
    }

    public EnemyControl(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
        super(level, nbt);
    }

    @Nullable
    @Override
    protected Set<? extends PrioritizedGoal> createBehaviorGoals(MobEntity mob) {
        return null;
    }

    @Nullable
    @Override
    protected Set<? extends PrioritizedGoal> createTargetGoals(MobEntity mob) {
        Type type = (Type) getType();
        NearestAttackableTargetGoal<MobEntity> targetGoal = new NearestAttackableTargetGoal<>(mob, MobEntity.class, type.randomInterval, type.mustSee, type.mustReach, type.predicate.and(otherMob -> canAttack(mob, otherMob)));
        ((INearestAttackableTargetGoalMixin<?>) targetGoal).setTargetConditions(((INearestAttackableTargetGoalMixin<?>) targetGoal).getTargetConditions().allowSameTeam());
        return ImmutableSet.<PrioritizedGoal>builder()
                .add(new PrioritizedGoal(1, new BetterHurtByTargetGoal(mob)))
                .add(new PrioritizedGoal(2, targetGoal))
                .build();
    }

    protected boolean canAttack(MobEntity mob, LivingEntity entity) {
        MobEntity otherMob = (MobEntity) entity;
        if (getSoulManager().isControlling(otherMob)) {
            return getSoulManager().getFlagIntersection(mob, otherMob).isEmpty();
        }
        return true;
    }

    @Nullable
    @Override
    protected Adjustment<?> getAdjustmentTowards(MobEntity otherMob, MobEntity mobUnderControl) {
        if (otherMob instanceof AbstractRaiderEntity && mobUnderControl instanceof AbstractRaiderEntity) {
            return Adjustments.RAIDERS;
        }
        if (otherMob instanceof GuardianEntity&& mobUnderControl instanceof GuardianEntity) {
            return Adjustments.GUARDIANS;
        }
        return super.getAdjustmentTowards(otherMob, mobUnderControl);
    }

    @Override
    protected Set<SensorType<? extends Sensor<? super MobEntity>>> getExtraSensors(MobEntity mob) {
        return ImmutableSet.of(ModSensors.NEAREST_MONSTERS);
    }

    @Override
    protected Map<Activity, List<BrainTaskHelper>> getControllableActivityMap(MobEntity mob) {
        return ImmutableMap.of(Activity.IDLE,
                BrainTaskHelper.singleton(new ControlledEnemyFindTargetTask<>(), BrainTaskHelper.replace(ForgetAttackTargetTask.class)),
                Activity.FIGHT,
                BrainTaskHelper.singleton(new ControlledEnemyFindNewTargetTask<>(mob), BrainTaskHelper.replace(FindNewAttackTargetTask.class)));
    }

    public static class Type extends ControlledMobBehavior<EnemyControl> {
        public final int randomInterval;
        public final boolean mustSee;
        public final boolean mustReach;
        public final Predicate<LivingEntity> predicate;

        public Type(ResourceLocation registryName) {
            this(registryName, 10, false, false, entity -> entity instanceof IMob);
        }

        private Type(ResourceLocation registryName, int randomInterval, boolean mustSee, boolean mustReach, Predicate<? super LivingEntity> predicate) {
            super(registryName);
            this.randomInterval = randomInterval;
            this.mustSee = mustSee;
            this.mustReach = mustReach;
            this.predicate = predicate::test;
        }

        public Type mustSee() {
            return new Type(getRegistryName(), randomInterval, true, mustReach, predicate);
        }

        public Type mustReach() {
            return new Type(getRegistryName(), randomInterval, mustSee, true, predicate);
        }

        public Type setRandomInterval(int randomInterval) {
            return new Type(getRegistryName(), randomInterval, mustSee, mustReach, predicate);
        }

        public Type setTargetConditions(Predicate<? super LivingEntity> predicate) {
            return new Type(getRegistryName(), randomInterval, mustSee, mustReach, predicate);
        }

        @Override
        public EnemyControl create(MobEntity mob, ServerWorld level) {
            return new EnemyControl(mob, level);
        }

        @Override
        public EnemyControl load(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            return new EnemyControl(level, nbt);
        }
    }
}
