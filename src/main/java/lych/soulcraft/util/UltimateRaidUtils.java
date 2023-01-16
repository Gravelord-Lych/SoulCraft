package lych.soulcraft.util;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.mixin.WaveMemberAccessor;
import lych.soulcraft.util.mixin.IRaidMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.Raid.WaveMember;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;

import static net.minecraft.entity.ai.attributes.Attributes.*;
import static net.minecraft.world.raid.Raid.WaveMember.create;

public final class UltimateRaidUtils {
    public static final Marker ULRAID = MarkerManager.getMarker("UltimateRaid");

    public static final WaveMember UL_DARK_EVOKER = create("UL_DARK_EVOKER", ModEntities.DARK_EVOKER, new int[]{0, 0, 0, 0, 0, 1, 0, 3, 0, 2, 0});
    public static final WaveMember UL_ENGINEER =    create("IL_ENGINEER",    ModEntities.ENGINEER,    new int[]{0, 0, 0, 2, 0, 0, 4, 0, 3, 0, 0});
    public static final WaveMember UL_EVOKER =      create("UL_EVOKER",      EntityType.EVOKER,       new int[]{0, 0, 0, 0, 1, 2, 0, 0, 0, 1, 0});
    public static final WaveMember UL_PILLAGER =    create("UL_PILLAGER",    EntityType.PILLAGER,     new int[]{0, 7, 5, 3, 7, 7, 5, 6, 7, 9, 0});
    public static final WaveMember UL_RAVAGER =     create("UL_RAVAGER",     EntityType.RAVAGER,      new int[]{0, 1, 0, 2, 0, 0, 3, 0, 2, 3, 10});
    public static final WaveMember UL_VINDICATOR =  create("UL_VINDICATOR",  EntityType.VINDICATOR,   new int[]{0, 4, 7, 6, 7, 3, 2, 5, 6, 4, 0});
    public static final WaveMember UL_WITCH =       create("UL_WITCH",       EntityType.WITCH,        new int[]{0, 0, 1, 0, 3, 0, 0, 2, 1, 2, 0});

    private static final List<WaveMember> WAVE_MEMBERS = new ArrayList<>();
    private static final Map<EntityType<?>, TriConsumer<? super AbstractRaiderEntity, ? super DifficultyInstance, ? super Raid>> CONSUMER_MAP = new HashMap<>();

    static {
        bind(UL_DARK_EVOKER, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid dark evoker health bonus", 0.5);
            applyModifier(raider, difficulty, FOLLOW_RANGE, "Ulraid dark evoker follow range bonus", 0.25);
        });
        bind(UL_ENGINEER, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid engineer health bonus", 0.5);
            applyModifier(raider, difficulty, MOVEMENT_SPEED, "Ulraid engineer speed bonus", 0.25);
        });
        bind(UL_EVOKER, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid evoker health bonus", 0.5);
            applyModifier(raider, difficulty, FOLLOW_RANGE, "Ulraid evoker follow range bonus", 0.25);
        });
        bind(UL_PILLAGER, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid pillager health bonus", 1);
            applyModifier(raider, difficulty, FOLLOW_RANGE, "Ulraid pillager follow range bonus", 0.2);
            applyModifier(raider, difficulty, MOVEMENT_SPEED, "Ulraid pillager speed bonus", 0.25);
        });
        bind(UL_RAVAGER, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid ravager health bonus", 0.5);
            applyModifier(raider, difficulty, ATTACK_DAMAGE, "Ulraid ravager damage bonus", 0.5);
            applyModifier(raider, difficulty, MOVEMENT_SPEED, "Ulraid ravager speed bonus", 0.15);
        });
        bind(UL_VINDICATOR, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid vindicator health bonus", 1);
            applyModifier(raider, difficulty, FOLLOW_RANGE, "Ulraid vindicator follow range bonus", 0.5);
            applyModifier(raider, difficulty, MOVEMENT_SPEED, "Ulraid vindicator speed bonus", 0.15);
        });
        bind(UL_WITCH, (raider, difficulty, raid) -> {
            applyModifier(raider, difficulty, MAX_HEALTH, "Ulraid witch health bonus", 1);
            applyModifier(raider, difficulty, FOLLOW_RANGE, "Ulraid witch follow range bonus", 0.25);
            applyModifier(raider, difficulty, MOVEMENT_SPEED, "Ulraid witch speed bonus", 0.25);
        });
    }

    private static void applyModifier(AbstractRaiderEntity raider, DifficultyInstance difficulty, Attribute attribute, String name, double amount) {
        amount *= getAmountMultiplier(difficulty);
        EntityUtils.addPermanentModifierIfAbsent(raider, attribute, new AttributeModifier(name, amount, AttributeModifier.Operation.MULTIPLY_TOTAL));
    }

    private static double getAmountMultiplier(DifficultyInstance difficulty) {
        switch (difficulty.getDifficulty()) {
            case EASY:
                return 0.5;
            case NORMAL:
                return 1;
            case HARD:
                return 1.5;
            default:
                return 0;
        }
    }

    private UltimateRaidUtils() {}

    public static void applyUlraidBuffsTo(AbstractRaiderEntity raider, WaveMember member, DifficultyInstance difficulty, Raid raid) {
        if (((IRaidMixin) raid).isUltimate()) {
            CONSUMER_MAP.getOrDefault(((WaveMemberAccessor) (Object) member).getEntityType(), (r, d, a) -> {}).accept(raider, difficulty, raid);
        }
    }

    public static WaveMember[] getWaveMemberArray() {
        return WAVE_MEMBERS.toArray(new WaveMember[0]);
    }

    public static void bind(WaveMember member, TriConsumer<? super AbstractRaiderEntity, ? super DifficultyInstance, ? super Raid> raiderConsumer) {
        registerWaveMember(member);
        CONSUMER_MAP.put(((WaveMemberAccessor) (Object) member).getEntityType(), raiderConsumer);
    }

    public static void registerWaveMember(WaveMember member) {
        Objects.requireNonNull(member);
        if (!WAVE_MEMBERS.contains(member)) {
            WAVE_MEMBERS.add(member);
        }
        SoulCraft.LOGGER.info(ULRAID, "WaveMember {} has been registered", member);
    }
}
