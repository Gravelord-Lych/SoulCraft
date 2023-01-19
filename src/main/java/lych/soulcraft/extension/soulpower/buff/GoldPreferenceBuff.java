package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum GoldPreferenceBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        tick(player);
    }

    @Override
    public void clientTick(ClientPlayerEntity player, ClientWorld world) {
        tick(player);
    }

    private static void tick(PlayerEntity player) {
        if (ExtraAbilityConstants.shouldApplyGoldPreference(player)) {
            ExtraAbilityConstants.GOLD_PREFERENCE_EFFECTS.stream().map(EffectInstance::new).forEach(player::addEffect);
        }
    }
}
