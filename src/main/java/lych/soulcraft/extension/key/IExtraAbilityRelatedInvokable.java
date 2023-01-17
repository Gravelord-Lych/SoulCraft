package lych.soulcraft.extension.key;

import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import net.minecraft.entity.player.ServerPlayerEntity;

public interface IExtraAbilityRelatedInvokable extends IConditionalInvokable {
    int SUCCESS = 1;
    int FAILURE = 0;

    @Override
    default void doAccept(ServerPlayerEntity player, int recentlyPressed) {
        int cooldown = ((IPlayerEntityMixin) player).getAdditionalCooldowns().getCooldownRemaining(requiredAbility().getRegistryName());
        if (cooldown == 0 && recentlyPressed == requiredRecentlyPressed() && getInvokeResult(player, recentlyPressed) == SUCCESS) {
            ((IPlayerEntityMixin) player).getAdditionalCooldowns().addCooldown(requiredAbility().getRegistryName(), getCooldown(player, recentlyPressed));
        }
    }

    @Override
    default boolean canUse(ServerPlayerEntity player, int recentlyPressed) {
        return requiredAbility().isOn(player);
    }

    IExtraAbility requiredAbility();

    int requiredRecentlyPressed();

    int getInvokeResult(ServerPlayerEntity player, int recentlyPressed);

    int getCooldown(ServerPlayerEntity player, int recentlyPressed);
}
