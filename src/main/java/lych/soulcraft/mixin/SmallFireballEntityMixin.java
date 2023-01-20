package lych.soulcraft.mixin;

import lych.soulcraft.extension.soulpower.control.SoulManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(SmallFireballEntity.class)
public abstract class SmallFireballEntityMixin extends AbstractFireballEntity {
    public SmallFireballEntityMixin(EntityType<? extends AbstractFireballEntity> type, World world) {
        super(type, world);
        throw new UnsupportedOperationException();
    }

    @Redirect(method = "onHitEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;fireImmune()Z"))
    private boolean shouldNotDealDamage(Entity entity) {
        SoulManager manager = SoulManager.get((ServerWorld) level);
        if (entity instanceof MobEntity && getOwner() instanceof MobEntity && manager.isControlling((MobEntity) getOwner())) {
//          Enables controlled blazes to deal damage to non-controlled blazes.
            return entity.fireImmune() && !manager.getFlagIntersection((MobEntity) entity, (MobEntity) getOwner()).isEmpty();
        }
        return entity.fireImmune();
    }
}
