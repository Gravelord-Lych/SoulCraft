package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public enum InitialArmorBuff implements PlayerBuff {
    INSTANCE;

    private static final UUID INITIAL_ARMOR_MODIFIER_UUID = UUID.fromString("7D0582A4-090A-72EF-8620-36B7F636DC05");
    private static final AttributeModifier INITIAL_ARMOR_MODIFIER = new AttributeModifier(INITIAL_ARMOR_MODIFIER_UUID, "Initial armor", 4, Operation.ADDITION);

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, Attributes.ARMOR).addPermanentModifier(INITIAL_ARMOR_MODIFIER);
    }

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {
        EntityUtils.getAttribute(player, Attributes.ARMOR).removeModifier(INITIAL_ARMOR_MODIFIER);
    }

    @Override
    public void tick(PlayerEntity player, ServerWorld world) {}
}
