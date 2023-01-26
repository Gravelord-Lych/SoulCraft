package lych.soulcraft.item.potion;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SplashPotionItem;
import net.minecraft.util.NonNullList;

public class HalfUsedSplashPotionItem extends SplashPotionItem {
    public HalfUsedSplashPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {}
}
