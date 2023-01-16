package lych.soulcraft.entity.monster.voidwalker;

import lych.soulcraft.api.shield.ISharedShield;
import lych.soulcraft.api.shield.IShieldUser;
import lych.soulcraft.entity.ai.goal.VoidwalkerGoals.FollowVoidwalkerGoal;
import lych.soulcraft.entity.ai.goal.VoidwalkerGoals.HealOthersGoal;
import lych.soulcraft.extension.shield.SharedShield;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.RedstoneParticles;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class VoidDefenderEntity extends VoidwalkerEntity implements IShieldUser {
    static final double PROTECTIVE_RANGE = 6;
    static final float PROTECTED_DAMAGE_MULTIPLIER = 0.8f;
    private static final double HEAL_RANGE = 5;
    private static final int HEAL_INTERVAL = 30;
    private static final float HEAL_AMOUNT = 2;
    private static final float HEAL_AMOUNT_ELITE = 3;

    @Nullable
    private ISharedShield sharedShield;
    private boolean shieldValid = true;

    public VoidDefenderEntity(EntityType<? extends VoidDefenderEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return createVoidwalkerAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 5);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(3, new FollowVoidwalkerGoal<>(this, VoidwalkerEntity.class, 10));
        goalSelector.addGoal(4, new FollowVoidwalkerGoal<>(this, VoidArcherEntity.class, 20));
        goalSelector.addGoal(6, new HealOthersGoal(this, HEAL_RANGE, HEAL_INTERVAL));
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        if (getSharedShield() != null && !isShieldValid() && getSharedShield().getHealth() > getSharedShield().getPassiveDefense() * 0.5) {
            shieldValid = true;
            onShieldRegenerated();
        }
    }

    @Override
    public void doHealTarget(AbstractVoidwalkerEntity healTarget) {
        healTarget.heal(getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? HEAL_AMOUNT_ELITE : HEAL_AMOUNT);
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty) {}

    @Override
    protected void strengthenSelf(VoidwalkerTier tier, DifficultyInstance difficulty, SpawnReason reason) {
        float absoluteDefense = 0;
        float regenAmount = 1;
        switch (tier) {
            case PARAGON:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(100);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(15);
                getNonnullAttribute(Attributes.ARMOR_TOUGHNESS).setBaseValue(3);
                getNonnullAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.28);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(48);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1);
                regenAmount = 2;
                absoluteDefense = 1;
                break;
            case ELITE:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(8);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(65);
                getNonnullAttribute(Attributes.ARMOR).setBaseValue(10);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(36);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.8);
                absoluteDefense = 1;
                break;
            case EXTRAORDINARY:
                getNonnullAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(6);
                getNonnullAttribute(Attributes.MAX_HEALTH).setBaseValue(35);
                getNonnullAttribute(Attributes.FOLLOW_RANGE).setBaseValue(30);
                getNonnullAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.6);
                break;
            default:
        }
        float passiveDefense = getMaxHealth();
        setHealth(getMaxHealth());
        sharedShield = new SharedShield(absoluteDefense, passiveDefense, 40, regenAmount, false);
    }

    @Override
    public double getAttackReachRadiusMultiplier() {
        switch (getTier()) {
            case PARAGON:
                return 1.5;
            case ELITE:
                return 1.2;
            case EXTRAORDINARY:
            case ORDINARY:
            default:
                return 1;
        }
    }

    @Override
    public boolean isProtected() {
        return true;
    }

    @Nullable
    @Override
    public ISharedShield getSharedShield() {
        return sharedShield;
    }

    @Override
    public void setSharedShield(@Nullable ISharedShield sharedShield) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isShieldValid() {
        return IShieldUser.super.isShieldValid() && shieldValid;
    }

    @Override
    public void onShieldExhausted() {
        if (!level.isClientSide()) {
            shieldValid = false;
            ((ServerWorld) level).sendParticles(ParticleTypes.EXPLOSION, getRandomX(1), getY(0.4 + random.nextDouble() * 0.2), getRandomZ(1), 1, 0, 0, 0, 0);
        }
    }

    private void onShieldRegenerated() {
        EntityUtils.addParticlesAroundSelfServerside(this, (ServerWorld) level, RedstoneParticles.CYAN, 6 + random.nextInt(5));
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        if (!level.isClientSide() && getSharedShield() != null) {
            compoundNBT.put("SharedShield", getSharedShield().save());
        }
        compoundNBT.putBoolean("ShieldValid", isShieldValid());
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compoundNBT) {
        super.readAdditionalSaveData(compoundNBT);
        if (!level.isClientSide() && compoundNBT.contains("SharedShield")) {
            sharedShield = new SharedShield(compoundNBT.getCompound("SharedShield"));
            Objects.requireNonNull(getSharedShield(), "SharedShield not present").setInvulnerableTicks(100);
        }
        if (compoundNBT.contains("ShieldValid")) {
            shieldValid = compoundNBT.getBoolean("ShieldValid");
        }
    }
}
