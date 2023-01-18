package lych.soulcraft.config;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.util.ExtraAbilityConstants;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID)
public class CommonConfig {
    public static final ForgeConfigSpec COMMON_CONFIG;
    static final ForgeConfigSpec.BooleanValue DISABLE_SE_BLOCKS_LOOT;
    static final ForgeConfigSpec.BooleanValue SHOW_BOSS_TIER;
    static final ForgeConfigSpec.BooleanValue STRICT_CHALLENGES;
    static final ForgeConfigSpec.BooleanValue TIERED_BOSSES;
    static final ForgeConfigSpec.IntValue CHECK_RECENTLY_PRESS_TIME;
    static final ForgeConfigSpec.IntValue ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();

        commonBuilder.push("Mob Settings");
        TIERED_BOSSES = commonBuilder
                .comment("If true, all mod bosses will be tiered (When you defeat them, they'll become stronger).")
                .define("tieredBosses", false);
        SHOW_BOSS_TIER = commonBuilder
                .comment("If true, all boss-tier will be shown.")
                .define("showBossTier", false);
        commonBuilder.pop();

        commonBuilder.push("Challenge Settings");
        STRICT_CHALLENGES = commonBuilder
                .comment("If true, cheats and unreasonable weapons will be disabled in the challenges.")
                .define("strictChallenges", false);
        commonBuilder.pop();

        commonBuilder.push("Block Settings");
        DISABLE_SE_BLOCKS_LOOT = commonBuilder
                .comment("If true, SE Generators and SE Storages will drop nothing when they are destroyed in Creative Mode, regardless of how much SE is inside them.")
                .define("disableSEBlocksLootIfCreative", false);
        commonBuilder.pop();

        commonBuilder.push("Key Input Settings");
        CHECK_RECENTLY_PRESS_TIME = commonBuilder
                .comment("The interval to check recently pressed keys, used for Extra Abilities like Dragon Wizard.")
                .defineInRange("checkRecentlyPressInterval (tick)", 10, 1, 20);
        commonBuilder.pop();

        commonBuilder.push("Extra Ability Settings");
        ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT = commonBuilder
                .comment("The additional pickup delay for items that are thrown by a player who has Ultrareach Extra Ability.")
                .defineInRange("ultrareachLengthenPickupDelayAmount (tick)", ExtraAbilityConstants.DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT, 0, ExtraAbilityConstants.DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT * 2);

        COMMON_CONFIG = commonBuilder.build();
    }
}
