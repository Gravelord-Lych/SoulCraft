package lych.soulcraft.entity.monster.voidwalker;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.util.EnumConstantNotFoundException;
import lych.soulcraft.util.IIdentifiableEnum;

import java.util.Arrays;

public enum VoidwalkerTier implements IIdentifiableEnum {
    ORDINARY,
    EXTRAORDINARY,
    ELITE,
    PARAGON;

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

    public VoidwalkerTier upgraded() {
        if (getId() == values().length - 1) {
            throw new IllegalStateException(String.format("VoidwalkerTier.%s is the strongest tier!", this));
        }
        return byId(getId() + 1);
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
            SoulCraft.LOGGER.warn(AbstractVoidwalkerEntity.VOIDWALKER, "Tier indexed {} is not supported, set to default", ORDINARY);
            return ORDINARY;
        }
    }

    public boolean isOrdinary() {
        return this == ORDINARY;
    }

    public String suffixTextureName(boolean ethereal, String commonSuffix, String etherealSuffix) {
        String suffix = ethereal ? etherealSuffix : commonSuffix;
        if (isOrdinary()) {
            return "_" + suffix;
        }
        return "_" + toString().toLowerCase() + "_" + suffix;
    }
}
