package lych.soulcraft.mixin.client;

import net.minecraft.client.gui.DisplayEffectsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DisplayEffectsScreen.class)
public class DisplayEffectsScreenMixin {
    @ModifyConstant(method = "renderLabels", constant = @Constant(intValue = 9))
    private int foo(int maxAmplifier) {
        return (maxAmplifier + 1) * 5 - 1; // <=50
    }
}
