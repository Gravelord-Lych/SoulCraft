package lych.soulcraft.util.mixin;

import lych.soulcraft.extension.soulpower.control.BrainTaskHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;

import java.util.List;

public interface IBrainMixin<E extends LivingEntity> {
    boolean isValidBrain();

    <U extends Sensor<? super E>> boolean addExtraSensor(SensorType<? extends U> type);

    boolean adjustTask(Activity activity, List<? extends BrainTaskHelper> taskHelper);

    void clearExtraSensors();

    void clearExtraTasks();
}
