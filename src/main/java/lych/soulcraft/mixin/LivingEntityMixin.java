package lych.soulcraft.mixin;

import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.entity.ModAttributes;
import lych.soulcraft.extension.soulpower.reinforce.FishReinforcement;
import lych.soulcraft.util.ModConstants;
import lych.soulcraft.util.mixin.IClientPlayerMixin;
import lych.soulcraft.util.mixin.ILivingEntityMixin;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ILivingEntityMixin {
    @Shadow public abstract ItemStack getMainHandItem();

    @Shadow public abstract Iterable<ItemStack> getArmorSlots();

    @Shadow public abstract double getAttributeValue(Attribute p_233637_1_);

    private long sheepReinforcementTickCount;
    @Unique
    private long sheepReinforcementLastHurtByTimestamp;
    /**
     * Knockup strength will only be used when the entity is knockbacked
     */
    private double knockupStrength;

    private LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Inject(method = "createLivingAttributes", at = @At(value = "RETURN"), cancellable = true)
    private static void createJumpStrengthAttribute(CallbackInfoReturnable<AttributeModifierMap.MutableAttribute> cir) {
        cir.setReturnValue(cir.getReturnValue().add(ModAttributes.JUMP_STRENGTH.get()));
    }

    @ModifyConstant(method = "getJumpPower", constant = @Constant(floatValue = 0.42f))
    private float useJumpStrengthAttributeValue(float constant) {
        return (float) getAttributeValue(ModAttributes.JUMP_STRENGTH.get());
    }

    @SuppressWarnings("ConstantConditions")
    @ModifyVariable(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;hasEffect(Lnet/minecraft/potion/Effect;)Z"), ordinal = 0)
    private float enhancedJump(float f) {
        if ((Object) this instanceof ClientPlayerEntity) {
            float strength = ((IClientPlayerMixin) this).getEnhancedJumpStrength();
            if (strength > 0) {
                return f + ModConstants.Exa.ENHANCED_AUTO_JUMP_COEFFICIENT * (strength - 1);
            }
        }
        return f;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tick()V", shift = At.Shift.AFTER))
    private void tickSheepReinforcement(CallbackInfo ci) {
        sheepReinforcementTickCount = tickCount;
    }

    @Override
    public long getSheepReinforcementLastHurtByTimestamp() {
        return sheepReinforcementLastHurtByTimestamp;
    }

    @Override
    public void setSheepReinforcementLastHurtByTimestamp(long sheepReinforcementLastHurtByTimestamp) {
        this.sheepReinforcementLastHurtByTimestamp = sheepReinforcementLastHurtByTimestamp;
    }

    @Inject(method = "hurt", at = @At("RETURN"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;actuallyHurt(Lnet/minecraft/util/DamageSource;F)V")))
    private void postHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        MinecraftForge.EVENT_BUS.post(new PostLivingHurtEvent((LivingEntity) (Object) this, source, amount, cir.getReturnValueZ()));
    }

    @ModifyVariable(method = "decreaseAirSupply", at = @At(value = "CONSTANT", args = {"intValue=0", "expandZeroConditions=GREATER_THAN_ZERO"}, ordinal = 0, shift = At.Shift.BEFORE), ordinal = 1)
    private int modifyDecreaseAirSupplyProbability(int i) {
        int ex = FishReinforcement.getFishReinforcementLevel(getArmorSlots());
        return i + ex;
    }

    @Inject(method = "knockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;setDeltaMovement(DDD)V", shift = At.Shift.AFTER))
    private void onKnockback(float strength, double ratioX, double ratioZ, CallbackInfo ci) {
        if (getKnockupStrength() > 0) {
            push(0, getKnockupStrength(), 0);
            setKnockupStrength(0);
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "HEAD"))
    private void saveMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        compoundNBT.putLong("SheepReinforcementTickCount", getSheepReinforcementTickCount());
        compoundNBT.putLong("SheepReinforcementLastHurtByTimestamp", getSheepReinforcementLastHurtByTimestamp());
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "HEAD"))
    private void loadMore(CompoundNBT compoundNBT, CallbackInfo ci) {
        setSheepReinforcementLastHurtByTimestamp(compoundNBT.getLong("SheepReinforcementLastHurtByTimestamp"));
        sheepReinforcementTickCount = compoundNBT.getLong("SheepReinforcementTickCount");
    }

    @Override
    public double getKnockupStrength() {
        return knockupStrength;
    }

    @Override
    public void setKnockupStrength(double knockupStrength) {
        this.knockupStrength = knockupStrength;
    }

    @Override
    public long getSheepReinforcementTickCount() {
        return sheepReinforcementTickCount;
    }
}
