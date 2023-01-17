package lych.soulcraft.extension.key;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface IConditionalInvokable extends IInvokable {
    @Override
    default void onKeyPressed(ServerPlayerEntity player, int recentlyPressed) {
        if (canUse(player, recentlyPressed)) {
            doAccept(player, recentlyPressed);
        }
    }

    boolean canUse(ServerPlayerEntity player, int recentlyPressed);

    void doAccept(ServerPlayerEntity player, int recentlyPressed);
}