package lych.soulcraft.mixin;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.extension.soulpower.control.ControlledMobData;
import lych.soulcraft.extension.soulpower.control.SoulManager;
import lych.soulcraft.extension.soulpower.control.controller.Adjustment;
import lych.soulcraft.extension.soulpower.control.controller.Adjustment.AdjInstance;
import lych.soulcraft.extension.soulpower.control.controller.ControlledMobBehavior;
import lych.soulcraft.util.Utils;
import lych.soulcraft.util.mixin.IBrainMixin;
import lych.soulcraft.util.mixin.IGoalSelectorMixin;
import lych.soulcraft.util.mixin.IMobEntityMixin;
import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity implements IMobEntityMixin {
    @Shadow @Final public GoalSelector goalSelector;
    @Shadow @Final public GoalSelector targetSelector;

    @Shadow protected abstract float getEquipmentDropChance(EquipmentSlotType p_205712_1_);

    @Unique
    private List<AdjInstance> adjustments;

    private MobEntityMixin(EntityType<? extends LivingEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasValidBrain() {
        return ((IBrainMixin<?>) brain).isValidBrain();
    }

    @Override
    public boolean isControlled() {
        if (level instanceof ServerWorld) {
            SoulManager manager = SoulManager.get((ServerWorld) level);
            return manager.isControlling((MobEntity) (Object) this);
        }
        return false;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void handleGoalSelectors(EntityType<?> type, World world, CallbackInfo ci) {
        ((IGoalSelectorMixin) goalSelector).setMob((MobEntity) (Object) this);
        ((IGoalSelectorMixin) goalSelector).setAlt(new GoalSelector(world.getProfilerSupplier()));
        ((IGoalSelectorMixin) targetSelector).setMob((MobEntity) (Object) this);
        ((IGoalSelectorMixin) targetSelector).setAlt(new GoalSelector(world.getProfilerSupplier()));
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/MobEntity;tickLeash()V"))
    private void tickAdjustments(CallbackInfo ci) {
        getAdjustmentsDirectly().forEach(AdjInstance::tick);
    }

    @Inject(method = "addAdditionalSaveData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V",
                    shift = At.Shift.AFTER
    ))
    private void saveMoreData(CompoundNBT compoundNBT, CallbackInfo ci) {
        ListNBT listNBT = new ListNBT();
        for (AdjInstance instance : getAdjustmentsDirectly()) {
            listNBT.add(instance.save());
        }
        compoundNBT.put("Adjustments", listNBT);
    }

    @Inject(method = "readAdditionalSaveData",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;readAdditionalSaveData(Lnet/minecraft/nbt/CompoundNBT;)V",
                    shift = At.Shift.AFTER
            ))
    private void loadMoreData(CompoundNBT compoundNBT, CallbackInfo ci) {
        if (compoundNBT.contains("Adjustments", Constants.NBT.TAG_LIST)) {
            getAdjustmentsDirectly().clear();
            ListNBT listNBT = compoundNBT.getList("Adjustments", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); i++) {
                try {
                    CompoundNBT singleNBT = listNBT.getCompound(i);
                    Adjustment<?> adjustment = (Adjustment<?>) ControlledMobBehavior.get(new ResourceLocation(singleNBT.getString("Type")));
                    if (adjustment == null) {
                        throw new NotLoadedException(String.format("Adjustment %s was not found", singleNBT.getString("Type")));
                    }
                    if (!level.isClientSide()) {
                        AdjInstance instance = adjustment.load((ServerWorld) level, singleNBT);
                        instance.prepareToControlMob();
                        getAdjustmentsDirectly().add(instance);
                    }
                } catch (NotLoadedException e) {
                    LOGGER.warn("Adjustment not loaded for mob {}. Exception: {}", this, Utils.getOrDefault(ExceptionUtils.getRootCause(e).getMessage(), ExceptionUtils.getRootCause(e).toString()));
                }
            }
        }
    }

    @Override
    public void addAdjustment(Adjustment<?> adjustment, ControlledMobData data) {
        if (!level.isClientSide()) {
            if (getAdjustmentsDirectly().stream().anyMatch(instance -> instance.getType() == adjustment)) {
                return;
            }
            Entity entity = ((ServerWorld) level).getEntity(data.getDirectController());
            if (entity instanceof MobEntity) {
                AdjInstance instance = adjustment.createAdjustment((MobEntity) (Object) this, (MobEntity) entity, (ServerWorld) level);
                getAdjustmentsDirectly().add(instance);
            }
        }
    }

    @Override
    public boolean hasAdjustments() {
        return !getAdjustmentsDirectly().isEmpty();
    }

    @Override
    public void removeAllAdjustments() {
        if (!level.isClientSide()) {
            Iterator<AdjInstance> itr = getAdjustmentsDirectly().iterator();
            while (itr.hasNext()) {
                AdjInstance instance = itr.next();
                instance.stopControllingMob((MobEntity) (Object) this);
                itr.remove();
            }
        }
    }

    @Override
    public void removeAllAdjustments(UUID mobCausedAdjustment) {
        if (!level.isClientSide()) {
            Iterator<AdjInstance> itr = getAdjustmentsDirectly().iterator();
            while (itr.hasNext()) {
                AdjInstance instance = itr.next();
                if (Objects.equals(instance.adjustedBy(), mobCausedAdjustment)) {
                    instance.stopControllingMob((MobEntity) (Object) this);
                    itr.remove();
                }
            }
        }
    }

    @Override
    public List<AdjInstance> getAdjustments() {
        return ImmutableList.copyOf(getAdjustmentsDirectly());
    }

    private List<AdjInstance> getAdjustmentsDirectly() {
        if (adjustments == null) {
            adjustments = new ArrayList<>();
        }
        return adjustments;
    }

    @Override
    public float callGetEquipmentDropChance(EquipmentSlotType slotType) {
        return getEquipmentDropChance(slotType);
    }
}
