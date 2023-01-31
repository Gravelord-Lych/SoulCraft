package lych.soulcraft.util.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;

public interface IBrainMixin<E extends LivingEntity> {
    boolean isValidBrain();

    <U extends Sensor<? super E>> boolean addExtraSensor(SensorType<? extends U> type);

    void clearExtraSensors();

    void clearExtraTasks();
}
