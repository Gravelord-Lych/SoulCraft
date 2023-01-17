package lych.soulcraft.extension.soulpower.buff;

import com.google.common.collect.ImmutableMap;
import lych.soulcraft.api.exa.PlayerBuff;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Map;
import java.util.UUID;

public enum SpeedupBuff implements AttributiveBuff {
    INSTANCE;

    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("F9F8A382-7873-46C0-90AD-E95A9904158C");
    private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(SPEED_MODIFIER_UUID, "Speedup", 0.15, AttributeModifier.Operation.MULTIPLY_BASE);

    @Override
    public Map<Attribute, AttributeModifier> getModifiers() {
        return ImmutableMap.of(Attributes.MOVEMENT_SPEED, SPEED_MODIFIER);
    }
}
