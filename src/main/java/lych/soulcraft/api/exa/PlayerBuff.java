package lych.soulcraft.api.exa;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public interface PlayerBuff {
    /**
     * Called when a player gets its corresponding {@link lych.soulcraft.api.exa.IExtraAbility ExtraAbility}.<br>
     * Will be called when reloading world, but will not be called when the player dies.
     * @param player The player
     * @param world The world
     */
    void startApplyingTo(PlayerEntity player, World world);

    /**
     * Called when a player loses its corresponding {@link lych.soulcraft.api.exa.IExtraAbility ExtraAbility}.<br>
     * Will not be called when syncing data.
     * @param player The player
     * @param world The world
     */
    void stopApplyingTo(PlayerEntity player, World world);

    void tick(PlayerEntity player, ServerWorld world);
}
