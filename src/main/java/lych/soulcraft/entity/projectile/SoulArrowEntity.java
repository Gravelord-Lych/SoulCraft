package lych.soulcraft.entity.projectile;

import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.item.ModItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class SoulArrowEntity extends AbstractArrowEntity {
    private double damageMultiplier = 1;

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world) {
        super(type, world);
    }

    public SoulArrowEntity(World world, double x, double y, double z) {
        this(ModEntities.SOUL_ARROW, world, x, y, z);
    }

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world, double x, double y, double z) {
        super(type, x, y, z, world);
    }

    public SoulArrowEntity(World world, LivingEntity owner) {
        this(ModEntities.SOUL_ARROW, world, owner);
    }

    public SoulArrowEntity(EntityType<? extends SoulArrowEntity> type, World world, LivingEntity owner) {
        super(type, owner, world);
    }

    @Override
    public void tick() {
        super.tick();
        if (!isSilentArrow() && level instanceof ServerWorld) {
            ((ServerWorld) level).sendParticles(ParticleTypes.SOUL, getX() + getDeltaMovement().x, getY() + getDeltaMovement().y + 0.05, getZ() + getDeltaMovement().z, 1, 0, 0.02, 0, 0.01);
        }
    }

    protected boolean isSilentArrow() {
        return false;
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        sendParticles(4, 8);
        doAreaOfEffectDamage(result);
    }

    protected void doAreaOfEffectDamage(EntityRayTraceResult result) {
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(result.getLocation().subtract(1.5, 2, 1.5), result.getLocation().add(1.5, 2, 1.5)), EntityPredicates.ATTACK_ALLOWED)) {
            super.onHitEntity(new EntityRayTraceResult(entity));
        }
    }

    protected void sendParticles(int minCount, int maxCount) {
        if (level instanceof ServerWorld) {
            ((ServerWorld) level).sendParticles(ParticleTypes.SOUL_FIRE_FLAME, getX() + random.nextGaussian() * 0.1, getY() + random.nextDouble() * 0.5, getZ() + random.nextGaussian() * 0.1, random.nextInt(maxCount - minCount + 1) + minCount, random.nextGaussian() * 0.05, random.nextGaussian() * 0.1, random.nextGaussian() * 0.05, 0.1);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(ModItems.SOUL_ARROW);
    }

    @Override
    public double getBaseDamage() {
        return super.getBaseDamage() * getDamageMultiplier();
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = Math.max(0, damageMultiplier);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
