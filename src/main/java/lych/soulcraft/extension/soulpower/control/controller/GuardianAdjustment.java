package lych.soulcraft.extension.soulpower.control.controller;

import com.google.common.collect.ImmutableSet;
import lych.soulcraft.entity.ai.goal.BetterHurtByTargetGoal;
import lych.soulcraft.extension.soulpower.control.BrainTaskHelper;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuardianAdjustment extends Adjustment.AdjInstance {
    public GuardianAdjustment(MobEntity mob, MobEntity mobCausedAdjustment, ServerWorld level) {
        super(Adjustments.GUARDIANS, mob, mobCausedAdjustment, level);
    }

    public GuardianAdjustment(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
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
        if (!(mob instanceof GuardianEntity)) {
            return null;
        }
        return ImmutableSet.of(new PrioritizedGoal(-1, new BetterHurtByTargetGoal(mob,
                BetterHurtByTargetGoal.HURT_BY_TARGETING.allowSameTeam().selector(hurtBy -> hurtBy instanceof MobEntity && getSoulManager().isControlling((MobEntity) hurtBy)))));
    }

    @Override
    protected boolean overrideTargetGoals(MobEntity mob) {
        return false;
    }

//  Guardians do not have brains.
    @Override
    protected Set<SensorType<? extends Sensor<? super MobEntity>>> getExtraSensors(MobEntity mob) {
        return Collections.emptySet();
    }
    @Override
    protected Map<Activity, List<BrainTaskHelper>> getControllableActivityMap(MobEntity mob) {
        return Collections.emptyMap();
    }

    public static class Type extends Adjustment<GuardianAdjustment> {
        public Type(ResourceLocation registryName) {
            super(registryName);
        }

        @Override
        public GuardianAdjustment createAdjustment(MobEntity mob, MobEntity mobCausedAdjustment, ServerWorld level) {
            return new GuardianAdjustment(mob, mobCausedAdjustment, level);
        }

        @Override
        public GuardianAdjustment load(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            return new GuardianAdjustment(level, nbt);
        }
    }
}
