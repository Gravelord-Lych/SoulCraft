package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.highlight.HighlighterType;
import lych.soulcraft.util.ExtraAbilityConstants;
import lych.soulcraft.util.Vectors;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum MonsterViewBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void tick(PlayerEntity player, ServerWorld world) {
        for (LivingEntity entity : world.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(ExtraAbilityConstants.MONSTER_VIEW_RANGE), e -> e instanceof IMob)) {
            if (entity.distanceToSqr(player) > ExtraAbilityConstants.MONSTER_VIEW_RANGE * ExtraAbilityConstants.MONSTER_VIEW_RANGE) {
                continue;
            }
            Vector3d vec = player.position().vectorTo(entity.position());
            Vector3d viewVec = player.getLookAngle();
            double angle = Vectors.getAngle(vec, viewVec);
            if (angle < 0.5 * Math.PI) {
                EntityHighlightManager.get(world).highlight(HighlighterType.MONSTER_VIEW, entity, 1);
            }
        }
    }
}
