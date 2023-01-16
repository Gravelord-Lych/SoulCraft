package lych.soulcraft.extension.soulpower.control.controller;

import com.google.common.collect.ImmutableSet;
import lych.soulcraft.entity.ai.goal.BetterHurtByTargetGoal;
import lych.soulcraft.extension.soulpower.control.GoalCapturer;
import lych.soulcraft.util.mixin.INearestAttackableTargetGoalMixin;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Predicate;

public class AutoCapturableEnemyControl extends EnemyControl {
    public AutoCapturableEnemyControl(MobEntity mob, ServerWorld level) {
        super(DefaultControllers.AUTO_ENEMY_CONTROL, mob, level);
    }

    public AutoCapturableEnemyControl(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
        super(level, nbt);
    }

    @Nullable
    @Override
    protected Set<? extends PrioritizedGoal> createTargetGoals(MobEntity mob) {
        Type type = (Type) getType();
        ImmutableSet.Builder<PrioritizedGoal> builder = ImmutableSet.builder();
        builder.add(new PrioritizedGoal(1, new BetterHurtByTargetGoal(mob)));
        NearestAttackableTargetGoal<?> targetGoal = GoalCapturer.captureTargetGoalForPlayer(mob.targetSelector).orElse(null);
        if (targetGoal == null) {
            targetGoal = new NearestAttackableTargetGoal<>(mob, MobEntity.class, type.randomInterval, type.mustSee, type.mustReach, type.predicate);
        }
        targetGoal = ((INearestAttackableTargetGoalMixin<?>) targetGoal).modifyType(MobEntity.class, type.predicate.and(otherMob -> canAttack(mob, otherMob)));
        ((INearestAttackableTargetGoalMixin<?>) targetGoal).setTargetConditions(((INearestAttackableTargetGoalMixin<?>) targetGoal).getTargetConditions().allowSameTeam());
        builder.add(new PrioritizedGoal(2, targetGoal));
        return builder.build();
    }

    public static class Type extends ControlledMobBehavior<AutoCapturableEnemyControl> {
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
        public AutoCapturableEnemyControl create(MobEntity mob, ServerWorld level) {
            return new AutoCapturableEnemyControl(mob, level);
        }

        @Override
        public AutoCapturableEnemyControl load(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            return new AutoCapturableEnemyControl(level, nbt);
        }
    }
}
