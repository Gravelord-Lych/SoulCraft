package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public enum SwimmerBuff implements PlayerBuff {
    INSTANCE;

    private static final UUID SWIM_SPEED_MODIFIER_UUID = UUID.fromString("AD6375DA-9674-AC66-B81C-4DCF4665869C");
    private static final AttributeModifier SWIM_SPEED_MODIFIER = new AttributeModifier(SWIM_SPEED_MODIFIER_UUID, "Swimmer", 1, AttributeModifier.Operation.MULTIPLY_TOTAL);

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, ForgeMod.SWIM_SPEED.get()).addPermanentModifier(SWIM_SPEED_MODIFIER);
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, ForgeMod.SWIM_SPEED.get()).removeModifier(SWIM_SPEED_MODIFIER);
    }

    @Override
    public void tick(PlayerEntity player, ServerWorld world) {

    }
}
