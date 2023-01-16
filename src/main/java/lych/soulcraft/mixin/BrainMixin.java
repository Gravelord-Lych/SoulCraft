package lych.soulcraft.mixin;

import lych.soulcraft.extension.soulpower.control.BrainTaskHelper;
import lych.soulcraft.util.mixin.IBrainMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> implements IBrainMixin<E> {
    @Shadow
    @Final
    private Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;
    @Shadow
    @Final
    private Map<Integer, Map<Activity, Set<Task<? super E>>>> availableBehaviorsByPriority;

    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;
    @Unique
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> extraSensors = new HashMap<>();
    @Unique
    private final Map<Activity, Set<Task<? super E>>> temporaryBehaviors = new HashMap<>();
    @Unique
    private final Map<Activity, Set<Task<? super E>>> disabledBehaviors = new HashMap<>();

    @Override
    public boolean isValidBrain() {
        return !sensors.isEmpty() && !memories.isEmpty();
    }

    @Override
    public <U extends Sensor<? super E>> boolean addExtraSensor(SensorType<? extends U> type) {
        if (sensors.containsKey(type)) {
            return false;
        }
        Sensor<? super E> sensor = type.create();
        extraSensors.put(type, sensor);
        for (MemoryModuleType<?> memory : sensor.requires()) {
            memories.putIfAbsent(memory, Optional.empty());
        }
        return true;
    }

    @Override
    public void clearExtraSensors() {
        extraSensors.clear();
    }

    @Override
    public void clearExtraTasks() {
        for (Map.Entry<Activity, Set<Task<? super E>>> entry : disabledBehaviors.entrySet()) {
            for (Map<Activity, Set<Task<? super E>>> activityMap : availableBehaviorsByPriority.values()) {
                Set<Task<? super E>> tasks = activityMap.computeIfAbsent(entry.getKey(), a -> new HashSet<>());
                tasks.addAll(entry.getValue());
                tasks.removeAll(temporaryBehaviors.get(entry.getKey()));
            }
        }
        disabledBehaviors.clear();
        temporaryBehaviors.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean adjustTask(Activity activity, List<? extends BrainTaskHelper> taskHelpers) {
        for (BrainTaskHelper taskHelper : taskHelpers) {
            for (Map<Activity, Set<Task<? super E>>> activityMap : availableBehaviorsByPriority.values()) {
                for (Map.Entry<Activity, Set<Task<? super E>>> entry : activityMap.entrySet()) {
                    if (entry.getKey() == activity) {
                        Set<Task<? super E>> set = entry.getValue();
                        Iterator<Task<? super E>> itr = set.iterator();
                        while (itr.hasNext()) {
                            Task<? super E> task = itr.next();
                            if (taskHelper.test(task)) {
                                itr.remove();
                                disabledBehaviors.computeIfAbsent(activity, a -> new HashSet<>()).add(task);
                            }
                        }
                        set.add((Task<? super E>) taskHelper.getTask());
                        temporaryBehaviors.computeIfAbsent(activity, a -> new HashSet<>()).add((Task<? super E>) taskHelper.getTask());
                    }
                }
            }
        }
        return false;
    }

    @Inject(method = "tickSensors", at = @At(value = "TAIL"))
    private void tickExtraSensors(ServerWorld level, E mob, CallbackInfo ci) {
        for (Sensor<? super E> sensor : extraSensors.values()) {
            sensor.tick(level, mob);
        }
    }
}
