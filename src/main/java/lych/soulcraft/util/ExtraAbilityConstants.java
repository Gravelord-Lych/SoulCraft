package lych.soulcraft.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import lych.soulcraft.extension.ExtraAbility;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;

public final class ExtraAbilityConstants {
//  Constant fields.
    public static final float ENHANCED_AUTO_JUMP_MAX_JUMP_HEIGHT_MULTIPLIER = 2;
    public static final float ENHANCED_AUTO_JUMP_COEFFICIENT = 0.19f;
    public static final float FALL_BUFFER_AMOUNT = 5;
    public static final double MONSTER_VIEW_RANGE = 16;
    public static final double BASE_TELEPORTATION_RADIUS = 19;
    public static final int TELEPORTATION_COOLDOWN = 300;
    public static final float FANGS_DAMAGE = 4;
    public static final double FANGS_SPACING = 1.25;
    public static final double FANGS_MAX_Y_OFFSET = 5;
    public static final int FANGS_SUMMONER_COUNT = 8;
    public static final double ULTRAREACH_HORIZONTAL_BONUS = 1;
    public static final double ULTRAREACH_VERTICAL_BONUS = 0.25;
    public static final int DEFAULT_ULTRAREACH_LENGTHEN_PICKUP_DELAY_AMOUNT = 20;
    public static final int WATER_BREATHING_TICKS = 300;
    public static final int WATER_BREATHING_TICKS_WITH_TURTLE_HELMET = 500;
    public static final float THORNS_MASTER_DAMAGE = 3;
    public static final double SPEEDUP_AMOUNT = 0.15;
    public static final double ULTRAREACH_AMOUNT = 1;
    public static final int RESTORATION_INTERVAL_TICKS = 200;
    public static final EffectInstance POISONER_POISON_EFFECT = new ImmutableEffectInstance(Effects.POISON, 20 * 4, 0);
    public static final double INITIAL_ARMOR_AMOUNT = 4;
    public static final int OVERDRIVE_FOOD_LEVEL_REQUIREMENT = 14;
    public static final int OVERDRIVE_REGEN_INTERVAL = 60;
    public static final double IMITATOR_VISIBILITY_MODIFIER = 0.6;
    public static final float NUTRITIONIST_NUTRITION_AND_SATURATION_MODIFIER = 1.25f;
    public static final ImmutableList<EffectInstance> GOLD_PREFERENCE_EFFECTS = ImmutableList.of(
            new ImmutableEffectInstance(Effects.DIG_SPEED, 20, 1, false, false, true),
            new ImmutableEffectInstance(Effects.MOVEMENT_SPEED, 20, 1, false, false, true));
    public static final int FROST_RESISTANCE_MONSTER_EFFECT_DURATION = 5;
    public static final int FROST_RESISTANCE_MONSTER_EFFECT_AMPLIFIER = 0;
    public static final double FROST_RESISTANCE_SLOWDOWN_RADIUS = 4;
    public static final int PILLAGER_LOOTING_LEVEL_BONUS = 1;

    private ExtraAbilityConstants() {}

//  Functions.
    public static float calculateNethermanAttackDamageMultiplier(float temperature, boolean onFire) {
        return MathHelper.clamp((1 + temperature / 5) * (onFire ? 1.25f : 1), 0.9f, 2);
    }

    public static float calculateFrostResistanceDamageMultiplier(float temperature, boolean onFire) {
        float damageMultiplier = MathHelper.clamp(0.5f + (temperature + 1) / 6, 0.5f, 1);
        return onFire ? (damageMultiplier + 1) / 2 : damageMultiplier;
    }

    public static boolean shouldApplyGoldPreference(PlayerEntity player) {
        return ExtraAbility.GOLD_PREFERENCE.isOn(player) && Streams.stream(player.getAllSlots()).anyMatch(ExtraAbilityConstants::isGold);
    }

    private static boolean isGold(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof TieredItem && ((TieredItem) item).getTier() == ItemTier.GOLD) {
            return true;
        }
        if (item instanceof ArmorItem && ((ArmorItem) item).getMaterial() == ArmorMaterial.GOLD) {
            return true;
        }
        return Utils.getRegistryName(item).getPath().contains("gold");
    }
}
