package lych.soulcraft.extension.key;

import lych.soulcraft.item.IModeChangeable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;

public enum ChangeModeInvokable implements IInvokable {
    INSTANCE;

    @Override
    public void onKeyPressed(ServerPlayerEntity player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IModeChangeable) {
            ((IModeChangeable) stack.getItem()).changeMode(stack, player);
        }
    }
}
