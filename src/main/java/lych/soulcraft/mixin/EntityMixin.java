package lych.soulcraft.mixin;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import lych.soulcraft.entity.iface.IHasOwner;
import lych.soulcraft.tag.ModFluidTags;
import lych.soulcraft.util.ModDamageSources;
import lych.soulcraft.util.ModDataSerializers;
import lych.soulcraft.util.SoulLavaConstants;
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

    @Unique
    private static final DataParameter<Boolean> DATA_ON_SOUL_FIRE = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
    @Unique
    private static final DataParameter<Boolean> DATA_REVERSED = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
    @Unique
    private static final DataParameter<Optional<Color>> DATA_HIGHLIGHT_COLOR = EntityDataManager.defineId(Entity.class, ModDataSerializers.OPTIONAL_COLOR);

    @Unique
    private boolean onSoulFire;
    @Unique
    private boolean reversed;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void defineExtraData(CallbackInfo ci) {
        entityData.define(DATA_HIGHLIGHT_COLOR, Optional.empty());
        entityData.define(DATA_ON_SOUL_FIRE, false);
        entityData.define(DATA_REVERSED, false);
        fluidHeight = new Object2DoubleArrayMap<>(4);
    }

    @Unique
    @Override
    public boolean isOnSoulFire() {
        if (isOnFire()) {
            boolean clientSide = level != null && level.isClientSide();
            return !fireImmune() && (onSoulFire || clientSide && entityData.get(DATA_ON_SOUL_FIRE));
        }
        return false;
    }

    @ModifyConstant(method = "lavaHurt", constant = @Constant(floatValue = 4))
    public float handleSoulLavaHurtDamage(float constant) {
        if (isInSoulLava()) {
            constant *= SoulLavaConstants.SOUL_LAVA_DAMAGE_MULTIPLIER;
        }
        return constant;
    }

    @Inject(method = "lavaHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;setSecondsOnFire(I)V", shift = At.Shift.AFTER))
    private void makeVictimOnSoulFire(CallbackInfo ci) {
        if (isInSoulLava()) {
            setOnSoulFire(true);
        }
    }

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void updateInSoulFireState(CallbackInfoReturnable<Boolean> cir, double d0, boolean flag) {
        cir.setReturnValue(updateFluidHeightAndDoFluidPushing(ModFluidTags.SOUL_LAVA, d0) | cir.getReturnValueZ());
    }

    @Unique
    private boolean isInSoulLava() {
        return !firstTick && fluidHeight.getDouble(ModFluidTags.SOUL_LAVA) > 0;
    }

    @Unique
    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean displaySoulFireAnimation() {
        return isOnFire() && isOnSoulFire() && !isSpectator();
    }

    @Unique
    @Override
    public void setOnSoulFire(boolean onSoulFire) {
        this.onSoulFire = onSoulFire;
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
        if (!isOnFire() && isOnSoulFire()) {
            setOnSoulFire(false);
            entityData.set(DATA_ON_SOUL_FIRE, false);
        }
        if (!level.isClientSide()) {
            if (getRemainingFireTicks() <= 0) {
                setOnSoulFire(false);
                entityData.set(DATA_ON_SOUL_FIRE, false);
            }
            entityData.set(DATA_ON_SOUL_FIRE, onSoulFire);
        }
        if (isInSoulLava()) {
            setOnSoulFire(true);
        }
    }

    @Inject(method = "clearFire", at = @At(value = "HEAD"))
    private void clearAllSoulFire(CallbackInfo ci) {
        setOnSoulFire(false);
//        entityData.set(DATA_ON_SOUL_FIRE, false);
    }

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getDeltaMovement()Lnet/minecraft/util/math/vector/Vector3d;"))
    private void saveSoulFireData(CompoundNBT compoundNBT, CallbackInfoReturnable<CompoundNBT> cir) {
        compoundNBT.putBoolean("OnSoulFire", isOnSoulFire());
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundNBT;getFloat(Ljava/lang/String;)F"))
    private void loadSoulFireData(CompoundNBT compoundNBT, CallbackInfo ci) {
        setOnSoulFire(compoundNBT.getBoolean("OnSoulFire"));
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
