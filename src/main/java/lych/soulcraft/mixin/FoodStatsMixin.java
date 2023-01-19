package lych.soulcraft.mixin;

import lych.soulcraft.extension.ExtraAbility;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(FoodStats.class)
public class FoodStatsMixin {
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 18))
    private int modifyRegenFoodLevelThreshold(int constant, PlayerEntity player) {
        if (ExtraAbility.OVERDRIVE.isOn(player)) {
            return ExtraAbilityConstants.OVERDRIVE_FOOD_LEVEL_REQUIREMENT;
        }
        return constant;
    }

    @ModifyConstant(method = "tick", constant = @Constant(intValue = 80, ordinal = 0))
    private int modifyRegenInterval(int constant, PlayerEntity player) {
        if (ExtraAbility.OVERDRIVE.isOn(player)) {
            return ExtraAbilityConstants.OVERDRIVE_REGEN_INTERVAL;
        }
        return constant;
    }
}
