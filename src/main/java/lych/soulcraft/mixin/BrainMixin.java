package lych.soulcraft.mixin;

import lych.soulcraft.util.mixin.IBrainMixin;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Optional;

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> implements IBrainMixin<E> {
    @Shadow
    @Final
    private Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors;
    @Shadow
    @Final
    private Map<MemoryModuleType<?>, Optional<? extends Memory<?>>> memories;

    @Shadow public abstract void stopAll(ServerWorld p_218227_1_, E p_218227_2_);

    @Unique
    private boolean disabled;

    @Override
    public boolean isValidBrain() {
        return !sensors.isEmpty() && !memories.isEmpty();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void noTick(ServerWorld world, E mob, CallbackInfo ci) {
        if (disabled) {
            stopAll(world, mob);
            ci.cancel();
        }
    }

    @Override
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
