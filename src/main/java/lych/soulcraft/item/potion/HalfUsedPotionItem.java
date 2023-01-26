package lych.soulcraft.item.potion;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.util.NonNullList;

public class HalfUsedPotionItem extends PotionItem {
    public HalfUsedPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {}
}
