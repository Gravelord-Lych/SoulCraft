package lych.soulcraft.extension.soulpower.control;

import com.google.common.base.MoreObjects;
import lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior;
import lych.soulcraft.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public class ControlledMobData {
    private final UUID mob;
    private final UUID controller;
    private final UUID directController;
    private final long controlTime;
    private final ControlledMobBehavior<?> behaviorType;

    public ControlledMobData(UUID mob, UUID controller, long controlTime, ControlledMobBehavior<?> behaviorType) {
        this(mob, controller, controller, controlTime, behaviorType);
    }

    public ControlledMobData(UUID mob, UUID controller, UUID directController, long controlTime, ControlledMobBehavior<?> behaviorType) {
        this.mob = mob;
        this.controller = controller;
        this.directController = directController;
        this.controlTime = controlTime;
        this.behaviorType = behaviorType;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ControlledMobData plusTime(long time) {
        return new ControlledMobData(mob, controller, directController, isInfiniteControlTime() ? Utils.INFINITY : controlTime + time, behaviorType);
    }

    public boolean isInfiniteControlTime() {
        return controlTime == Utils.INFINITY;
    }

    public UUID getMob() {
        return mob;
    }

    public UUID getController() {
        return controller;
    }

    public UUID getDirectController() {
        return directController;
    }

    public long getControlTime() {
        return controlTime;
    }

    public ControlledMobBehavior<?> getBehaviorType() {
        return behaviorType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("mob", mob)
                .add("controller", controller)
                .add("directController", directController)
                .add("controlTime", controlTime)
                .add("behaviorType", behaviorType)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ControlledMobData)) return false;
        ControlledMobData data = (ControlledMobData) o;
        return getControlTime() == data.getControlTime() && Objects.equals(getMob(), data.getMob()) && Objects.equals(getController(), data.getController()) && Objects.equals(getDirectController(), data.getDirectController()) && Objects.equals(getBehaviorType(), data.getBehaviorType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMob(), getController(), getDirectController(), getControlTime(), getBehaviorType());
    }

    public void saveTo(CompoundNBT compoundNBT) {
        compoundNBT.putUUID("ControlledMob", mob);
        compoundNBT.putUUID("Controller", controller);
        compoundNBT.putUUID("DirectController", directController);
        compoundNBT.putLong("ControlTime", controlTime);
        compoundNBT.putString("BehaviorType", behaviorType.getRegistryName().toString());
    }

    public static ControlledMobData loadFrom(CompoundNBT compoundNBT) {
        UUID mob = compoundNBT.getUUID("ControlledMob");
        UUID controller = compoundNBT.getUUID("Controller");
        UUID directController = compoundNBT.getUUID("DirectController");
        long controlTime = compoundNBT.getLong("ControlTime");
        ResourceLocation registryName = new ResourceLocation(compoundNBT.getString("BehaviorType"));
        ControlledMobBehavior<?> behaviorType = ControlledMobBehavior.get(registryName);
        Objects.requireNonNull(behaviorType, "Unknown behavior type: " + registryName);
        return builder().setMob(mob).setController(controller).setDirectController(directController).setControlTime(controlTime).setBehaviorType(behaviorType).build();
    }

    public static class Builder {
        private UUID mob;
        private UUID controller;
        private UUID directController;
        private long controlTime;
        private ControlledMobBehavior<?> behaviorType;

        public Builder setMob(UUID mob) {
            this.mob = mob;
            return this;
        }

        public Builder setController(UUID controller) {
            if (this.controller == null) {
                setDirectController(controller);
            }
            this.controller = controller;
            return this;
        }

        public Builder setDirectController(@Nullable UUID directController) {
            if (directController != null) {
                this.directController = directController;
            }
            return this;
        }

        public Builder setControlTime(long controlTime) {
            this.controlTime = controlTime;
            return this;
        }

        public Builder setBehaviorType(ControlledMobBehavior<?> behaviorType) {
            this.behaviorType = behaviorType;
            return this;
        }

        public ControlledMobData build() {
            return new ControlledMobData(mob, controller, directController, controlTime, behaviorType);
        }
    }
}
