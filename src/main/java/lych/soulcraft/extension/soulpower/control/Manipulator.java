package lych.soulcraft.extension.soulpower.control;

import lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;

public class Manipulator<T extends LivingEntity> implements INBTSerializable<CompoundNBT> {
    private final T controller;
    private final double range;
    private final World level;
    private final Random random;
    private final EntityPredicate canControl;
    private final Function<? super MobEntity, ? extends ControlledMobBehavior<?>> howToControl;
    private final Function<? super MobEntity, ? extends ControlFlag> flagGetter;
    @Nullable
    private MobEntity victim;
    private boolean active;
    private boolean stopped;

    public Manipulator(T controller,
                       double range,
                       Predicate<? super MobEntity> canControl,
                       Function<? super MobEntity, ? extends ControlledMobBehavior<?>> howToControl,
                       Function<? super MobEntity, ? extends ControlFlag> flagGetter) {
        this(controller,
                range,
                EntityPredicate.DEFAULT.selector(entity -> entity instanceof MobEntity && canControl.test((MobEntity) entity)),
                howToControl,
                flagGetter);
    }

    public Manipulator(T controller,
                       double range,
                       EntityPredicate canControl,
                       Function<? super MobEntity, ? extends ControlledMobBehavior<?>> howToControl,
                       Function<? super MobEntity, ? extends ControlFlag> flagGetter) {
        this.controller = controller;
        this.level = controller.level;
        this.random = controller.getRandom();
        this.range = range;
        this.canControl = canControl;
        this.howToControl = howToControl;
        this.flagGetter = flagGetter;
    }

    public void tick() {
        if (stopped || level.isClientSide()) {
            return;
        }
        if (!EntityUtils.isAlive(victim)) {
            active = false;
            findNewVictim();
        } else {
            active = true;
            ServerWorld world = (ServerWorld) level;
            SoulManager.get(world).control(victim,
                    ControlledMobData.builder()
                            .setMob(victim.getUUID())
                            .setBehaviorType(howToControl.apply(victim))
                            .setController(controller.getUUID())
                            .setControlTime(Utils.INFINITY)
                            .build(),
                    ControlOptions.defaultSettings(flagGetter.apply(victim)));
        }
    }

    public void stop() {
        if (!level.isClientSide() && victim != null) {
            SoulManager.get((ServerWorld) level).stopControlling(victim);
            active = false;
            stopped = true;
        }
    }

    public void restart() {
        stopped = false;
    }

    private void findNewVictim() {
        victim = level.getNearbyEntities(MobEntity.class, canControl, controller, controller.getBoundingBox().inflate(range))
                .stream()
                .filter(Entity::isAlive)
                .min(Comparator.comparingDouble(mob -> mob.distanceToSqr(controller)))
                .orElse(null);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putBoolean("Active", active);
        compoundNBT.putBoolean("Stopped", stopped);
        if (victim != null) {
            compoundNBT.putInt("Victim", victim.getId());
        }
        return compoundNBT;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        active = nbt.getBoolean("Active");
        stopped = nbt.getBoolean("Stopped");
        if (nbt.contains("Victim")) {
            Entity entity = level.getEntity(nbt.getInt("Victim"));
            if (EntityUtils.isAlive(entity) && entity instanceof MobEntity) {
                victim = (MobEntity) entity;
            }
        }
    }
}
