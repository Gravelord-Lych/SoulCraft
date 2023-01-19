package lych.soulcraft.extension.soulpower.buff;

import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum WaterBreathingBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void tick(PlayerEntity player, ServerWorld world) {
        if (!player.isEyeInFluid(FluidTags.WATER)) {
            boolean turtleHelmeted = player.getItemBySlot(EquipmentSlotType.HEAD).getItem() == Items.TURTLE_HELMET;
            player.addEffect(new EffectInstance(Effects.WATER_BREATHING, turtleHelmeted ? ExtraAbilityConstants.WATER_BREATHING_TICKS_WITH_TURTLE_HELMET : ExtraAbilityConstants.WATER_BREATHING_TICKS, 0, false, false, true));
        }
    }
}
