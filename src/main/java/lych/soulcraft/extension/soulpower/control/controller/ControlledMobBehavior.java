package lych.soulcraft.extension.soulpower.control.controller;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.extension.soulpower.control.BrainTaskHelper;
import lych.soulcraft.extension.soulpower.control.ControlledMobData;
import lych.soulcraft.extension.soulpower.control.SoulManager;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.mixin.IBrainMixin;
import lych.soulcraft.util.mixin.IGoalSelectorMixin;
import lych.soulcraft.util.mixin.IMobEntityMixin;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public abstract class ControlledMobBehavior<T extends ControlledMobBehavior.Instance> {
    public static final ControlledMobBehavior<?> DUMMY;

    private static final Map<ResourceLocation, ControlledMobBehavior<?>> BEHAVIOR_MAP;
    private final ResourceLocation registryName;

    protected ControlledMobBehavior(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    public static <T extends Instance> ControlledMobBehavior<T> register(ControlledMobBehavior<T> behavior) {
        Preconditions.checkState(BEHAVIOR_MAP.putIfAbsent(behavior.registryName, behavior) == null, "Duplicate registryName: " + behavior.registryName);
        return behavior;
    }

    @Nullable
    public static ControlledMobBehavior<?> get(ResourceLocation registryName) {
        return BEHAVIOR_MAP.get(registryName);
    }

    public static ImmutableMap<ResourceLocation, ControlledMobBehavior<?>> getAllBehaviors() {
        return ImmutableMap.copyOf(BEHAVIOR_MAP);
    }

    public abstract T create(MobEntity mob, ServerWorld level);

    public abstract T load(ServerWorld level, CompoundNBT nbt) throws NotLoadedException;

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("registryName", registryName)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlledMobBehavior)) return false;
        ControlledMobBehavior<?> that = (ControlledMobBehavior<?>) o;
        return Objects.equals(getRegistryName(), that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegistryName());
    }

    public static abstract class Instance {
        private final ControlledMobBehavior<?> type;
        protected final UUID mob;
        protected final ServerWorld level;
        private boolean preparing;

        protected Instance(ControlledMobBehavior<?> type, MobEntity mob, ServerWorld level) {
            this.type = type;
            this.mob = mob.getUUID();
            this.level = level;
        }

        protected Instance(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            this.level = level;
            try {
                this.mob = Objects.requireNonNull(nbt.getUUID("Mob"));
                this.type = Objects.requireNonNull(get(new ResourceLocation(nbt.getString("Type"))));
            } catch (Exception e) {
                throw new NotLoadedException(e);
            }
        }

        protected Optional<MobEntity> getMob() {
            return Optional.ofNullable((MobEntity) level.getEntity(mob));
        }

        protected final SoulManager getSoulManager() {
            return SoulManager.get(level);
        }

        public final void prepareToControlMob() {
            preparing = true;
        }

        @SuppressWarnings("unchecked")
        public void startControllingMob(MobEntity mob) {
            clearAdjustments(mob);

            if (hasValidBrain(mob)) {
                for (SensorType<? extends Sensor<? super MobEntity>> sensor : getExtraSensors(mob)) {
//                  The cast is safe because all entities that have valid brains are mobs.
                    ((IBrainMixin<? extends MobEntity>) mob.getBrain()).addExtraSensor(sensor);
                }
                for (Map.Entry<Activity, List<BrainTaskHelper>> entry : getControllableActivityMap(mob).entrySet()) {
                    ((IBrainMixin<? extends MobEntity>) mob.getBrain()).adjustTask(entry.getKey(), entry.getValue());
                }
                return;
            }
            Set<? extends PrioritizedGoal> mobGoals = createBehaviorGoals(mob);
            Set<? extends PrioritizedGoal> mobTargetGoals = createTargetGoals(mob);

            IGoalSelectorMixin goalSelector = (IGoalSelectorMixin) mob.goalSelector;
            IGoalSelectorMixin targetSelector = (IGoalSelectorMixin) mob.targetSelector;

            if (mobGoals != null) {
                mobGoals.forEach(goal -> EntityUtils.directlyAddGoal(Objects.requireNonNull(goalSelector.getAlt()), goal));
                if (!overrideBehaviorGoals(mob)) {
                    goalSelector.transferGoals();
                }
            } else {
//              Copy goals to altSelector
                goalSelector.transferGoals();
            }

            if (mobTargetGoals != null) {
                mobTargetGoals.forEach(goal -> EntityUtils.directlyAddGoal(Objects.requireNonNull(targetSelector.getAlt()), goal));
                if (!overrideTargetGoals(mob)) {
                    targetSelector.transferGoals();
                }
            } else {
                targetSelector.transferGoals();
            }
        }

        protected void clearAdjustments(MobEntity mob) {
            ((IMobEntityMixin) mob).removeAllAdjustments();
        }

        @SuppressWarnings("unchecked")
        public void stopControllingMob(MobEntity mob) {
            if (hasValidBrain(mob)) {
                ((IBrainMixin<? extends MobEntity>) mob.getBrain()).clearExtraTasks();
                ((IBrainMixin<? extends MobEntity>) mob.getBrain()).clearExtraSensors();

                mob.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                return;
            }

            IGoalSelectorMixin goalSelector = (IGoalSelectorMixin) mob.goalSelector;
            goalSelector.removeAllAltGoals();

            IGoalSelectorMixin targetSelector = (IGoalSelectorMixin) mob.targetSelector;
            targetSelector.removeAllAltGoals();

            mob.setTarget(null);
            removeAdjustments();
        }

        public CompoundNBT save() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putUUID("Mob", mob);
            compoundNBT.putString("Type", type.registryName.toString());
            return compoundNBT;
        }

        public void tick() {
            if (preparing) {
                getMob().ifPresent(mobUnderControl -> {
                    startControllingMob(mobUnderControl);
                    preparing = false;
                });
            }
            applyAdjustments();
        }

        protected void applyAdjustments() {
            getMob().ifPresent(mobUnderControl -> {
                for (MobEntity otherMob : mobUnderControl.level.getEntitiesOfClass(MobEntity.class, mobUnderControl.getBoundingBox().inflate(getAdjustRange()))) {
                    if (otherMob == mobUnderControl) {
                        continue;
                    }
                    Adjustment<?> adjustment = getAdjustmentTowards(otherMob, mobUnderControl);
                    if (adjustment == null) {
                        continue;
                    }
                    if (getSoulManager().isControlling(otherMob) && !adjustment.canApplyToControlledMob(otherMob, mobUnderControl)) {
                        continue;
                    }
                    SoulManager manager = getSoulManager();
                    ControlledMobData oData = Objects.requireNonNull(manager.getData(mob));
                    ((IMobEntityMixin) otherMob).addAdjustment(adjustment,
                            ControlledMobData.builder()
                                    .setMob(otherMob.getUUID())
                                    .setController(oData.getController())
                                    .setDirectController(mob)
                                    .setBehaviorType(adjustment)
                                    .setControlTime(oData.getControlTime())
                                    .build());
                }
            });
        }

        protected void removeAdjustments() {
            level.getEntities()
                    .filter(entity -> entity instanceof MobEntity)
                    .map(entity -> (MobEntity) entity)
                    .filter(otherMob -> ((IMobEntityMixin) otherMob).hasAdjustments())
                    .filter(otherMob -> ((IMobEntityMixin) otherMob).getAdjustments().stream().anyMatch(instance -> Objects.equals(instance.adjustedBy(), mob)))
                    .forEach(otherMob -> ((IMobEntityMixin) otherMob).removeAllAdjustments(mob));
        }

        @Nullable
        protected Adjustment<?> getAdjustmentTowards(MobEntity otherMob, MobEntity mobUnderControl) {
            return null;
        }

        protected double getAdjustRange() {
            return 16;
        }

        public ControlledMobBehavior<?> getType() {
            return type;
        }

        // If returns null, all behavior goals will not be replaced
        @Nullable
        protected abstract Set<? extends PrioritizedGoal> createBehaviorGoals(MobEntity mob);

        // If returns null, all target goals will not be replaced
        @Nullable
        protected abstract Set<? extends PrioritizedGoal> createTargetGoals(MobEntity mob);

        protected boolean overrideBehaviorGoals(MobEntity mob) {
            return true;
        }

        protected boolean overrideTargetGoals(MobEntity mob) {
            return true;
        }

//      ----------------------------------------FOR BRAINS----------------------------------------
        protected static boolean hasValidBrain(MobEntity mob) {
            return ((IMobEntityMixin) mob).hasValidBrain();
        }

        protected abstract Set<SensorType<? extends Sensor<? super MobEntity>>> getExtraSensors(MobEntity mob);

        protected abstract Map<Activity, List<BrainTaskHelper>> getControllableActivityMap(MobEntity mob);
//      ------------------------------------------------------------------------------------------
    }
    
    private static class Dummy extends ControlledMobBehavior<DummyInst> {
        public Dummy() {
            super(SoulCraft.prefix("dummy"));
        }

        @Override
        public DummyInst create(MobEntity mob, ServerWorld level) {
            return new DummyInst(mob, level);
        }

        @Override
        public DummyInst load(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            return new DummyInst(level, nbt);
        }
    }

    private static class DummyInst extends Instance {
        public DummyInst(MobEntity mob, ServerWorld level) {
            super(DUMMY, mob, level);
        }

        public DummyInst(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
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
            return null;
        }

        @Override
        protected Set<SensorType<? extends Sensor<? super MobEntity>>> getExtraSensors(MobEntity mob) {
            return Collections.emptySet();
        }

        @Override
        protected Map<Activity, List<BrainTaskHelper>> getControllableActivityMap(MobEntity mob) {
            return Collections.emptyMap();
        }
    }

    static {
        BEHAVIOR_MAP = new HashMap<>();
        DUMMY = new Dummy();
        DefaultControllers.init();
    }
}
