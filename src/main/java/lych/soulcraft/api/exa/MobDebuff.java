package lych.soulcraft.api.exa;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface MobDebuff {
    /**
     * Called when {@link net.minecraftforge.event.entity.EntityJoinWorldEvent EntityJoinWorldEvent} is fired,
     * which means that the method will be called when the mob is summoned or reloaded.
     * You will cause chunk loading deadlocks if you don't delay your world interactions.
     * @param mob The mob
     * @param world The world of the mob
     */
    void doWhenMobJoinWorld(MobEntity mob, World world);

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

    void tick(MobEntity mob, World world);

    /**
     * Returns the max stack size of this debuff. For instance, debuff will be applied twice
     * ({@link MobDebuff#doWhenMobJoinWorld(MobEntity, World)} will be called twice) if 2 is
     * returned and there are 2 or more players has this.
     * @return The max stack size of this debuff
     */
    default int getMaxStackSize() {
        return 1;
    }
}
