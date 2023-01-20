package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

public enum WitherReachBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        if (player.isSpectator() || player.tickCount % ExtraAbilityConstants.WITHER_REACH_DAMAGE_INTERVAL != 0) {
            return;
        }
        double reachDistance = ExtraAbilityConstants.BASE_WITHER_REACH_DISTANCE + player.getAttributeValue(ForgeMod.REACH_DISTANCE.get());
        Vector3d position = player.getEyePosition(0);
        Vector3d viewVector = player.getViewVector(1);
        Vector3d targetPos = position.add(viewVector.scale(reachDistance));
        AxisAlignedBB possibleEntities = player.getBoundingBox().expandTowards(viewVector.scale(reachDistance)).inflate(1);
        EntityRayTraceResult ray = ProjectileHelper.getEntityHitResult(player, position, targetPos, possibleEntities, entity -> !entity.isSpectator() && entity.isPickable(), reachDistance * reachDistance);
        if (ray != null) {
            Entity target = ray.getEntity();
            if (target instanceof MobEntity && target instanceof IMob || target == player.getLastHurtMob()) {
                target.hurt(DamageSource.WITHER, ExtraAbilityConstants.WITHER_REACH_DAMAGE);
                ((LivingEntity) target).setLastHurtByMob(player);
                player.setLastHurtMob(target);
            }
        }
    }
}
