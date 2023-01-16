package lych.soulcraft.world.event.challenge;

import lych.soulcraft.item.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

import java.util.function.Supplier;

public enum ChallengeMedalType {
    IRON(0, false, false, Rarity.COMMON, () -> new ItemStack(ModItems.IRON_CHALLENGE_MEDAL)),
    GOLD(1, true, false, Rarity.UNCOMMON, () -> new ItemStack(ModItems.GOLD_CHALLENGE_MEDAL)),
    DIAMOND(2, true, false, Rarity.RARE, () -> new ItemStack(ModItems.DIAMOND_CHALLENGE_MEDAL)),
    NETHERITE(3, true, false, Rarity.EPIC, () -> new ItemStack(ModItems.NETHERITE_CHALLENGE_MEDAL));

    private final int id;
    private final boolean foil;
    private final boolean soulFoil;
    private final Rarity rarity;
    private final Supplier<ItemStack> medalFactory;

    ChallengeMedalType(int id, boolean foil, boolean soulFoil, Rarity rarity, Supplier<ItemStack> medalFactory) {
        this.id = id;
        this.foil = foil;
        this.soulFoil = soulFoil;
        this.rarity = rarity;
        this.medalFactory = medalFactory;
    }

    public int getId() {
        return id;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public boolean isFoil() {
        return foil;
    }

    public boolean isSoulFoil() {
        return soulFoil;
    }

    public ChallengeMedalType byId(int id) {
        for (ChallengeMedalType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid id: " + id);
    }

    public ItemStack createMedal() {
        return medalFactory.get();
    }
}
