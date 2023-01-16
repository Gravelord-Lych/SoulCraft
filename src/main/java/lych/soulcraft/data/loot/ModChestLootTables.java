package lych.soulcraft.data.loot;

import lych.soulcraft.SoulCraft;
import net.minecraft.data.loot.ChestLootTables;
import net.minecraft.util.ResourceLocation;

public class ModChestLootTables extends ChestLootTables {
    public static final ResourceLocation SURVIVAL_CHALLENGE = prefix("challenge/survival_challenge");

    private static ResourceLocation prefix(String name) {
        return SoulCraft.prefix("chests/" + name);
    }
}
