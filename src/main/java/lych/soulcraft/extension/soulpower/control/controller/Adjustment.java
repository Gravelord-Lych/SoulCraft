package lych.soulcraft.extension.soulpower.control.controller;

import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Adjustment<T extends Adjustment.AdjInstance> extends ControlledMobBehavior<T> {
    protected Adjustment(ResourceLocation registryName) {
        super(registryName);
    }

    @Override
    public final T create(MobEntity mob, ServerWorld level) {
        throw new UnsupportedOperationException("Missing mobCausedAdjustment");
    }

    public boolean canApplyToControlledMob(MobEntity otherMob, MobEntity mobUnderControl) {
        return false;
    }

    public abstract T createAdjustment(MobEntity mob, MobEntity mobCausedAdjustment, ServerWorld level);

    public static abstract class AdjInstance extends ControlledMobBehavior.Instance {
        protected final UUID mobCausedAdjustment;

        protected AdjInstance(Adjustment<? extends AdjInstance> type, MobEntity mob, MobEntity mobCausedAdjustment, ServerWorld level) {
            super(type, mob, level);
            this.mobCausedAdjustment = mobCausedAdjustment.getUUID();
        }

        protected AdjInstance(ServerWorld level, CompoundNBT nbt) throws NotLoadedException {
            super(level, nbt);
            this.mobCausedAdjustment = nbt.getUUID("MobCausedAdjustment");
        }

        @Override
        protected final void applyAdjustments() {}

        @Override
        protected final void removeAdjustments() {}

        @Nullable
        @Override
        protected final Adjustment<?> getAdjustmentTowards(MobEntity otherMob, MobEntity mobUnderControl) {
            return null;
        }

        @Override
        protected final double getAdjustRange() {
            return -1;
        }

        @Override
        public CompoundNBT save() {
            CompoundNBT compoundNBT = super.save();
            compoundNBT.putUUID("MobCausedAdjustment", mobCausedAdjustment);
            return compoundNBT;
        }

        public UUID adjustedBy() {
            return mobCausedAdjustment;
        }

        @Override
        public Adjustment<?> getType() {
            return (Adjustment<?>) super.getType();
        }
    }
}
