package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;

public interface AttributiveBuff extends PlayerBuff {
    Map<Attribute, AttributeModifier> getModifiers();

    @Override
    default void startApplyingTo(PlayerEntity player, World world) {
        getModifiers().forEach((attr, modifier) -> EntityUtils.getAttribute(player, attr).addPermanentModifier(modifier));
    }

    @Override
    default void stopApplyingTo(PlayerEntity player, World world) {
        getModifiers().forEach((attr, modifier) -> EntityUtils.getAttribute(player, attr).removeModifier(modifier));
    }

    @Override
    default void serverTick(ServerPlayerEntity player, ServerWorld world) {}
}
