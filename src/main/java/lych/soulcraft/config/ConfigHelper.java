package lych.soulcraft.config;

public final class ConfigHelper {
    private ConfigHelper() {}

    public static boolean isBossesTiered() {
        return CommonConfig.TIERED_BOSSES.get();
    }

    public static boolean shouldShowBossTier() {
        return CommonConfig.SHOW_BOSS_TIER.get();
    }

    public static boolean strictChallengesEnabled() {
        return CommonConfig.STRICT_CHALLENGES.get();
    }

    public static boolean canSEBlocksLoot() {
        return !CommonConfig.DISABLE_SE_BLOCKS_LOOT.get();
    }
}
