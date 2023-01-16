package lych.soulcraft.item;

import lych.soulcraft.SoulCraft;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public final class ModItemGroups {
    public static final ItemGroup DEFAULT = new ItemGroup(SoulCraft.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.SOUL_POWDER);
        }
    };
    public static final ItemGroup MACHINE = new ItemGroup(SoulCraft.MOD_ID + "_machine") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.SEGEN);
        }
    };

    private ModItemGroups() {}
}
