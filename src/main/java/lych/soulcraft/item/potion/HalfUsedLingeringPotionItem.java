package lych.soulcraft.item.potion;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.util.NonNullList;

public class HalfUsedLingeringPotionItem extends ThrowablePotionItem {
    public HalfUsedLingeringPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {}
}
