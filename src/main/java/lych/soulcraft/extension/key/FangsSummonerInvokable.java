package lych.soulcraft.extension.key;

import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.extension.ExtraAbility;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

import java.util.Objects;

public enum FangsSummonerInvokable implements IExtraAbilityRelatedInvokable {
    INSTANCE;

    @Override
    public IExtraAbility requiredAbility() {
        return ExtraAbility.FANGS_SUMMONER;
    }

    @Override
    public int requiredRecentlyPressed() {
        return -1;
    }

    @Override
    public int getInvokeResult(ServerPlayerEntity player, int recentlyPressed) {
        double minY = player.getY() - ExtraAbilityConstants.FANGS_MAX_Y_OFFSET;
        double maxY = player.getY() + ExtraAbilityConstants.FANGS_MAX_Y_OFFSET;
        Vector3d lookAngle = player.getLookAngle();
        lookAngle = new Vector3d(lookAngle.x, 0, lookAngle.z).normalize();
        if (Objects.equals(lookAngle, Vector3d.ZERO)) {
            return FAILURE;
        }
        double yRot = MathHelper.atan2(lookAngle.z, lookAngle.x);
        boolean success = false;
        for (int i = 0; i < ExtraAbilityConstants.FANGS_SUMMONER_COUNT; ++i) {
            double spacing = ExtraAbilityConstants.FANGS_SPACING * (i + 1);
            Vector3d newVectorToTarget = lookAngle.scale(spacing);
            success |= createFangs(player, newVectorToTarget, minY, maxY, yRot, i);
        }
        return success ? SUCCESS : FAILURE;
    }

    private static boolean createFangs(ServerPlayerEntity player, Vector3d vectorToTarget, double minY, double maxY, double yRot, int i) {
        return EntityUtils.createFangs(player.getX() + vectorToTarget.x, player.getZ() + vectorToTarget.z, minY, maxY, (float) yRot, i + 1, player, player.getLevel(), fangs -> fangs.setDamage(ExtraAbilityConstants.FANGS_DAMAGE));
    }

    @Override
    public int getCooldown(ServerPlayerEntity player, int recentlyPressed) {
        return 40;
    }
}
