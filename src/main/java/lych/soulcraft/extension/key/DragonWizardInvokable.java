package lych.soulcraft.extension.key;

import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.entity.GravitationalDragonFireballEntity;
import lych.soulcraft.extension.ExtraAbility;
import net.minecraft.entity.player.ServerPlayerEntity;

public enum DragonWizardInvokable implements IExtraAbilityRelatedInvokable {
    INSTANCE;

    @Override
    public IExtraAbility requiredAbility() {
        return ExtraAbility.DRAGON_WIZARD;
    }

    @Override
    public int requiredRecentlyPressed() {
//      Double-click
        return ModKeyInputs.DRAGON_WIZARD_KEY.getKey().getValue();
    }

    @Override
    public int getInvokeResult(ServerPlayerEntity player, int recentlyPressed) {
        GravitationalDragonFireballEntity fireball = new GravitationalDragonFireballEntity(player, player.level);
        fireball.shootFromRotation(player, player.xRot, player.yRot, 0, 0.75f, 1);
        if (player.getLevel().addFreshEntity(fireball)) {
            return SUCCESS;
        }
        return FAILURE;
    }

    @Override
    public int getCooldown(ServerPlayerEntity player, int recentlyPressed) {
        return 100;
    }
}
