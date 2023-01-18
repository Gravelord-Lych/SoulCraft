package lych.soulcraft.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import lych.soulcraft.entity.iface.IHasOwner;
import lych.soulcraft.extension.fire.Fire;
import lych.soulcraft.extension.fire.Fires;
import lych.soulcraft.util.ModDamageSources;
import lych.soulcraft.util.ModDataSerializers;
import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tags.ITag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.Optional;
import java.util.Random;

@Mixin(Entity.class)
public abstract class EntityMixin implements IEntityMixin {
    private static final int FIRE_FLAG = 0;
    private static final float LAVA_HURT_DAMAGE = 4;
    @Shadow
    @Final
    protected EntityDataManager entityData;

    @Shadow
    public abstract boolean fireImmune();

    @Shadow
    public World level;

    @Shadow
    @Final
    protected Random random;

    @Shadow public abstract int getRemainingFireTicks();

    @Shadow public abstract boolean isOnFire();

    @Shadow public abstract boolean isSpectator();

    @Shadow protected boolean firstTick;

    @Shadow protected Object2DoubleMap<ITag<Fluid>> fluidHeight;

    @Shadow public abstract boolean updateFluidHeightAndDoFluidPushing(ITag<Fluid> tag, double movementScale);

    @Shadow public abstract boolean hurt(DamageSource source, float amount);

    @Shadow public abstract AxisAlignedBB getBoundingBox();

    @Shadow protected abstract boolean getSharedFlag(int p_70083_1_);

    @Shadow protected abstract void setSharedFlag(int p_70052_1_, boolean p_70052_2_);

    @Shadow public abstract void setRemainingFireTicks(int p_241209_1_);

    @Shadow public abstract void remove();

    @Unique
    private static final DataParameter<Integer> DATA_FIRE_ID = EntityDataManager.defineId(Entity.class, DataSerializers.INT);
    @Unique
    private static final DataParameter<Boolean> DATA_REVERSED = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
    @Unique
    private static final DataParameter<Optional<Color>> DATA_HIGHLIGHT_COLOR = EntityDataManager.defineId(Entity.class, ModDataSerializers.OPTIONAL_COLOR);
    @Unique
    private boolean reversed;
    @Unique
    @NotNull
    private Fire fireOnSelf = Fires.empty();

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void defineExtraData(CallbackInfo ci) {
        entityData.define(DATA_HIGHLIGHT_COLOR, Optional.empty());
        entityData.define(DATA_FIRE_ID, Fires.empty().getId());
        entityData.define(DATA_REVERSED, false);
        fluidHeight = new Object2DoubleArrayMap<>(4);
    }

    @Unique
    @Override
    public boolean isOnSoulFire() {
        if (isOnFire()) {
            return !fireImmune() && getFireOnSelf() == Fires.SOUL_FIRE;
        }
        return false;
    }

    @Inject(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSecondsOnFire(I)V", shift = At.Shift.AFTER))
    private void makeVictimOnSoulFire(CallbackInfo ci) {
        adjustLava();
    }

    @ModifyConstant(method = "lavaHurt", constant = @Constant(floatValue = LAVA_HURT_DAMAGE))
    public float handleLavaHurtDamage(float constant) {
        return constant * getFireOnSelf().getFireDamage((Entity) (Object) this, level);
    }

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void updateInSoulFireState(CallbackInfoReturnable<Boolean> cir, double d0, boolean flag) {
        for (Fire fire : Fire.getTrueFires()) {
            cir.setReturnValue(updateFluidHeightAndDoFluidPushing(fire.getLavaTag(), d0) | cir.getReturnValueZ());
        }
    }

    @Unique
    private void adjustLava() {
        if (firstTick) {
            return;
        }
        Fire.getTrueFires().stream().filter(fire -> fluidHeight.getDouble(fire.getLavaTag()) > 0).findFirst().ifPresent(this::setFireOnSelf);
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean displaySoulFireAnimation() {
        return isOnFire() && isOnSoulFire() && !isSpectator();
    }

    @Deprecated
    @Unique
    @Override
    public void setOnSoulFire(boolean onSoulFire) {
        setFireOnSelf(onSoulFire ? Fires.SOUL_FIRE : Fires.empty());
    }

    @Override
    public Fire getFireOnSelf() {
        return level.isClientSide() ? Fire.byId(entityData.get(DATA_FIRE_ID)) : fireOnSelf;
    }

    @Override
    public void setFireOnSelf(Fire fire) {
        if (fire.canReplace(getFireOnSelf())) {
            fireOnSelf = fire;
            if (fire.isRealFire()) {
                entityData.set(DATA_FIRE_ID, fire.getId());
            }
        }
    }

    @Unique
    @Override
    public boolean isReversed() {
        return level.isClientSide() ? entityData.get(DATA_REVERSED) : reversed;
    }

    @Unique
    @Override
    public void setReversed(boolean reversed) {
        this.reversed = reversed;
        entityData.set(DATA_REVERSED, reversed);
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInLava()Z", ordinal = 1))
    private void synchronizeData(CallbackInfo ci) {
        if (getRemainingFireTicks() <= 0) {
            setFireOnSelf(Fires.empty());
        }
        adjustLava();
        getFireOnSelf().entityOnFire((Entity) (Object) this);
    }

    @Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSharedFlag(IZ)V", shift = At.Shift.AFTER))
    private void setFireStatusForClient(CallbackInfo ci) {
        entityData.set(DATA_FIRE_ID, getFireOnSelf().getId());
    }

    @Inject(method = "setRemainingFireTicks", at = @At("TAIL"))
    private void updateFire(int ticks, CallbackInfo ci) {
        if (ticks > 0 && !getFireOnSelf().isRealFire()) {
            setFireOnSelf(Fires.FIRE);
        }
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDeltaMovement()Lnet/minecraft/util/math/vector/Vector3d;"))
    private void saveFireData(CompoundNBT compoundNBT, CallbackInfoReturnable<CompoundNBT> cir) {
        getFireOnSelf().writeToNBT(compoundNBT, "EntityFireType");
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;getFloat(Ljava/lang/String;)F"))
    private void loadFireData(CompoundNBT compoundNBT, CallbackInfo ci) {
        if (compoundNBT.contains("EntityFireType", Constants.NBT.TAG_STRING)) {
            setFireOnSelf(Fire.fromNBT(compoundNBT, "EntityFireType"));
        }
    }

    @Inject(method = "thunderHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hurt(Lnet/minecraft/util/DamageSource;F)Z"), cancellable = true)
    private void handleThunderHit(ServerWorld world, LightningBoltEntity bolt, CallbackInfo ci) {
        LivingEntity owner = ((IHasOwner<?>) bolt).getOwner();
        if (owner != null) {
            hurt(ModDamageSources.indirectLightning(owner, bolt), bolt.getDamage());
            ci.cancel();
        }
    }

    @Override
    public Optional<Color> getHighlightColor() {
        return entityData.get(DATA_HIGHLIGHT_COLOR);
    }

    @Override
    public void setHighlightColor(@Nullable Color highlightColor) {
        entityData.set(DATA_HIGHLIGHT_COLOR, Optional.ofNullable(highlightColor));
    }

    @Override
    public boolean callGetSharedFlag(int flag) {
        return getSharedFlag(flag);
    }
}
