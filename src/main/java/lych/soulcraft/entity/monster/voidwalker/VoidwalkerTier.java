package lych.soulcraft.entity.monster.voidwalker;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.item.ModRarities;
import lych.soulcraft.util.DefaultValues;
import lych.soulcraft.util.EnumConstantNotFoundException;
import lych.soulcraft.util.IIdentifiableEnum;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;

public enum VoidwalkerTier implements IIdentifiableEnum {
    ORDINARY(Rarity.COMMON),
    EXTRAORDINARY(Rarity.RARE),
    ELITE(Rarity.EPIC),
    PARAGON(ModRarities.LEGENDARY);

    private final Rarity rarity;

    VoidwalkerTier(Rarity rarity) {
        this.rarity = rarity;
    }

    /**
     * @param min Inclusive
     * @param max Exclusive
     */
    public boolean isInRange(VoidwalkerTier min, VoidwalkerTier max) {
        return getId() >= min.getId() && getId() < max.getId();
    }

    /**
     * @param min Inclusive
     * @param max Inclusive
     */
    public boolean isInRangeClosed(VoidwalkerTier min, VoidwalkerTier max) {
        return getId() >= min.getId() && getId() <= max.getId();
    }

    public boolean strongerThan(VoidwalkerTier another) {
        return getId() > another.getId();
    }

    public boolean weakerThan(VoidwalkerTier another) {
        return getId() < another.getId();
    }

    public VoidwalkerTier upgraded() {
        if (isStrongest()) {
            throw new IllegalStateException(String.format("VoidwalkerTier.%s is the strongest tier!", this));
        }
        return byId(getId() + 1);
    }

    public VoidwalkerTier next() {
        return isStrongest() ? ORDINARY : upgraded();
    }

    public boolean isStrongest() {
        return getId() == values().length - 1;
    }

    public static VoidwalkerTier byId(int id) {
        return byId(id, true);
    }

    public static VoidwalkerTier byId(int id, boolean failhard) {
        try {
            return IIdentifiableEnum.byOrdinal(values(), id);
        } catch (EnumConstantNotFoundException e) {
            if (failhard) {
                throw new IllegalStateException(String.format("Tier indexed %s is not supported, available tiers are %s", e.getId(), Arrays.toString(values())));
            }
            SoulCraft.LOGGER.warn(AbstractVoidwalkerEntity.VOIDWALKER, "Tier indexed {} is not supported, set to default", id);
            return ORDINARY;
        }
    }

    public boolean isOrdinary() {
        return this == ORDINARY;
    }

    public String suffixTextureName(boolean ethereal, String commonSuffix, String etherealSuffix) {
        String suffix = ethereal ? etherealSuffix : commonSuffix;
        suffix = suffix.isEmpty() ? suffix : "_" + suffix;
        if (isOrdinary()) {
            return suffix;
        }
        return "_" + toString().toLowerCase() + suffix;
    }

    public ITextComponent getDescription(boolean withSpace) {
        return new TranslationTextComponent("voidwalker.tier." + toString().toLowerCase()).append(withSpace ? DefaultValues.SPACE : DefaultValues.dummyTextComponent());
    }

    public ITextComponent makeSpawnEggDescription(Item spawnEgg) {
        return getDescription(true).copy().append(spawnEgg.getDescription().copy());
    }

    public Rarity getRarity() {
        return rarity;
    }
}
