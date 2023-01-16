package lych.soulcraft.entity.passive;

import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.entity.ai.controller.VoidwalkerMovementController;
import lych.soulcraft.entity.iface.ESVMob;
import lych.soulcraft.entity.iface.IEtherealable;
import lych.soulcraft.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

public class IllusoryHorseEntity extends AbstractHorseEntity implements ESVMob, IEtherealable {
    private static final DataParameter<Boolean> DATA_ETHEREAL = EntityDataManager.defineId(IllusoryHorseEntity.class, DataSerializers.BOOLEAN);
    @Nullable
    private Vector3d sneakTarget;

    public IllusoryHorseEntity(EntityType<? extends IllusoryHorseEntity> type, World world) {
        super(type, world);
        moveControl = new VoidwalkerMovementController<>(this);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_ETHEREAL, false);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 30)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24);
    }

    @Override
    public int getTemper() {
        if (isVoidwalkersNearby()) {
            return 0;
        }
        return super.getTemper();
    }

    @Override
    protected boolean isImmobile() {
        if (ESVMob.isESVMob(getControllingPassenger())) {
            return false;
        }
        return super.isImmobile();
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        boolean retameable = isTamed() && getOwnerUUID() == null && isVehicle() && getPassengers().get(0) instanceof PlayerEntity;
        if (retameable && random.nextInt(50) == 0) {
            tameWithName((PlayerEntity) getPassengers().get(0));
        }
    }

    @Nullable
    @Override
    public AgeableEntity getBreedOffspring(ServerWorld world, AgeableEntity parent) {
        return ModEntities.ILLUSORY_HORSE.create(world);
    }

    @Override
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getOwnerUUID() == null && isVoidwalkersNearby()) {
            return ActionResultType.PASS;
        }
        if (isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (player.isSecondaryUseActive()) {
            openInventory(player);
            return ActionResultType.sidedSuccess(level.isClientSide());
        }
        if (isVehicle()) {
            return super.mobInteract(player, hand);
        }
        if (!stack.isEmpty()) {
            if (stack.getItem() == Items.SADDLE && !isSaddled()) {
                openInventory(player);
                return ActionResultType.sidedSuccess(level.isClientSide());
            }
            ActionResultType type = stack.interactLivingEntity(player, this, hand);
            if (type.consumesAction()) {
                return type;
            }
        }
        doPlayerRide(player);
        return ActionResultType.sidedSuccess(level.isClientSide());
    }

    @Override
    public double getPassengersRidingOffset() {
        return super.getPassengersRidingOffset() - 0.1875;
    }

    @Nullable
    @Override
    public ILivingEntityData finalizeSpawn(IServerWorld world, DifficultyInstance difficulty, SpawnReason reason, @Nullable ILivingEntityData data, @Nullable CompoundNBT compoundNBT) {
        double r = random.nextDouble();
        inventory.setItem(0, new ItemStack(Items.SADDLE));
        EntityType<? extends AbstractVoidwalkerEntity> type;
        if (r < 0.25) {
            type = ModEntities.VOIDWALKER;
        } else if (r < 0.5) {
            type = ModEntities.VOID_ARCHER;
        } else if (r < 0.75) {
            type = ModEntities.VOID_DEFENDER;
        } else {
            type = ModEntities.VOID_ALCHEMIST;
        }
        spawnRider(type, world, reason);
        return super.finalizeSpawn(world, difficulty, reason, data, compoundNBT);
    }

    private void spawnRider(EntityType<? extends AbstractVoidwalkerEntity> type, IServerWorld world, SpawnReason reason) {
        AbstractVoidwalkerEntity voidwalker = type.create(world.getLevel());
        if (voidwalker != null) {
            setTamed(true);
            voidwalker.moveTo(blockPosition(), 0, 0);
            voidwalker.finalizeSpawn(world, world.getCurrentDifficultyAt(blockPosition()), reason, null, null);
            voidwalker.startRiding(this);
        }
    }

    @Override
    protected void randomizeAttributes() {
        EntityUtils.getAttribute(this, Attributes.JUMP_STRENGTH).setBaseValue(generateRandomJumpStrength());
    }

    @Override
    public boolean canBeAffected(EffectInstance effect) {
        if (EntityUtils.isHarmful(effect)) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source == DamageSource.IN_WALL) {
            return false;
        }
        return super.hurt(source, amount);
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier) {
        return false;
    }

    private boolean isVoidwalkersNearby() {
        double followRange = getAttributeValue(Attributes.FOLLOW_RANGE);
        return level.getNearestEntity(AbstractVoidwalkerEntity.class, EntityUtils.ALL, this, getX(), getY(), getZ(), getBoundingBox().inflate(followRange)) != null;
    }

    @Override
    public boolean isEthereal() {
        return entityData.get(DATA_ETHEREAL) && (level.isClientSide() || getSneakTarget() != null);
    }

    @Override
    public double getSizeForCalculation() {
        return getBoundingBox().getSize();
    }

    private void setEthereal(boolean ethereal) {
        entityData.set(DATA_ETHEREAL, ethereal);
        noPhysics = ethereal;
        setNoGravity(ethereal);
    }

    @Nullable
    @Override
    public Vector3d getSneakTarget() {
        return sneakTarget;
    }

    @Override
    public boolean setSneakTarget(@Nullable Vector3d sneakTarget) {
        this.sneakTarget = sneakTarget;
        setEthereal(sneakTarget != null);
        return true;
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        compoundNBT.putBoolean("Ethereal", isEthereal());
        if (getSneakTarget() != null) {
            Vector3d et = getSneakTarget();
            compoundNBT.put("EtheTarget", newDoubleList(et.x, et.y, et.z));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (compoundNBT.contains("EtheTarget", Constants.NBT.TAG_LIST)) {
            ListNBT etn = compoundNBT.getList("EtheTarget", Constants.NBT.TAG_DOUBLE);
            setSneakTarget(new Vector3d(etn.getDouble(0), etn.getDouble(1), etn.getDouble(2)));
        }
        setEthereal(compoundNBT.getBoolean("Ethereal"));
    }
}
