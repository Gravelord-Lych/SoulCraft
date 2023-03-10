package lych.soulcraft.entity.monster;

import lych.soulcraft.entity.ai.goal.CopyOwnerTargetGoal;
import lych.soulcraft.entity.ai.goal.FollowOwnerGoal;
import lych.soulcraft.entity.iface.IHasOwner;
import lych.soulcraft.entity.monster.boss.SoulSkeletonKingEntity;
import lych.soulcraft.extension.fire.Fires;
import lych.soulcraft.item.ModItems;
import lych.soulcraft.util.ModSoundEvents;
import lych.soulcraft.util.mixin.IEntityMixin;
import lych.soulcraft.util.mixin.IGoalSelectorMixin;
import lych.soulcraft.world.gen.biome.sll.SLLayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SoulSkeletonEntity extends AbstractSkeletonEntity implements IHasOwner<SoulSkeletonKingEntity>, IPurifiable {
    private static final DataParameter<Boolean> DATA_PURIFIED = EntityDataManager.defineId(SoulSkeletonEntity.class, DataSerializers.BOOLEAN);
    private UUID ownerUUID;

    public SoulSkeletonEntity(EntityType<? extends SoulSkeletonEntity> type, World world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_PURIFIED, false);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(4, new FollowOwnerGoal<>(this, 1, 10, 4, false));
        targetSelector.addGoal(2, new CopyOwnerTargetGoal<>(this));
        ((IGoalSelectorMixin) targetSelector).getAvailableGoals().removeIf(goal -> goal.getGoal() instanceof HurtByTargetGoal);
        targetSelector.addGoal(1, new HurtByTargetGoal(this, SoulSkeletonKingEntity.class, SoulSkeletonEntity.class));
    }

    @Override
    public boolean isPurified() {
        return entityData.get(DATA_PURIFIED);
    }

    @Override
    public void setPurified(boolean purified) {
        entityData.set(DATA_PURIFIED, purified);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractSkeletonEntity.createAttributes();
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return ModSoundEvents.SOUL_SKELETON_AMBIENT.get();
    }

    @Override
    protected SoundEvent getStepSound() {
        return ModSoundEvents.SOUL_SKELETON_STEP.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSoundEvents.SOUL_SKELETON_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSoundEvents.SOUL_SKELETON_DEATH.get();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (!target.fireImmune()) {
                target.setSecondsOnFire(5);
                ((IEntityMixin) target).setFireOnSelf(isPurified() ? Fires.PURE_SOUL_FIRE : Fires.SOUL_FIRE);
            }
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance instance, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        if (world.getBiomeName(blockPosition()).map(SLLayer::getId).map(SLLayer::isPure).orElse(false)) {
            setPurified(true);
        }
        return super.finalizeSpawn(world, instance, reason, data, compoundNBT);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance instance) {
        setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(random.nextBoolean() ? ModItems.REFINED_SOUL_METAL_SWORD : ModItems.SOUL_BOW));
    }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack stack, float power) {
        AbstractArrowEntity arrow = ModItems.SOUL_ARROW.createArrow(level, stack, this);
        arrow.setEnchantmentEffectsFromEntity(this, power);
        if (stack.getItem() == Items.TIPPED_ARROW && arrow instanceof ArrowEntity) {
            ((ArrowEntity) arrow).setEffectsFromItem(stack);
        }
        arrow.setSecondsOnFire(100);
        ((IEntityMixin) arrow).setFireOnSelf(isPurified() ? Fires.PURE_SOUL_FIRE : Fires.SOUL_FIRE);
        return arrow;
    }

    @Override
    public void playSound(SoundEvent sound, float volume, float pitch) {
        super.playSound(sound == SoundEvents.SKELETON_SHOOT ? ModSoundEvents.SOUL_SKELETON_SHOOT.get() : sound, volume, pitch);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        saveOwner(compoundNBT);
        compoundNBT.putBoolean("Purified", isPurified());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        loadOwner(compoundNBT);
        setPurified(compoundNBT.getBoolean("Purified"));
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }

    @Nullable
    @Override
    public UUID getOwnerUUID() {
        return ownerUUID;
    }

    @Override
    public void setOwnerUUID(@Nullable UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }
}
