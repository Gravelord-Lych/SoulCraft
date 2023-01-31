package lych.soulcraft.extension;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.api.exa.MobDebuff;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.api.exa.SCExaNames;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.extension.soulpower.buff.*;
import lych.soulcraft.extension.soulpower.debuff.FrostResistanceDebuff;
import lych.soulcraft.extension.soulpower.debuff.MobDebuffMap;
import lych.soulcraft.extension.soulpower.debuff.MonsterSabotageDebuff;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcement;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static lych.soulcraft.SoulCraft.prefix;

public class ExtraAbility implements IExtraAbility {
    public static final Marker MARKER = MarkerManager.getMarker("ExtraAbilities");
    public static final IExtraAbility ARMOR_PIERCER = create(prefix(SCExaNames.ARMOR_PIERCER));
    public static final IExtraAbility BOW_EXPERT = create(prefix(SCExaNames.BOW_EXPERT), 5);
    public static final IExtraAbility CHEMIST = create(prefix(SCExaNames.CHEMIST));
    public static final IExtraAbility CLIMBER = create(prefix(SCExaNames.CLIMBER), 6);
    public static final IExtraAbility DESTROYER = create(prefix(SCExaNames.DESTROYER), 5);
    public static final IExtraAbility DRAGON_WIZARD = createSpecial(prefix(SCExaNames.DRAGON_WIZARD));
    public static final IExtraAbility ENHANCED_AUTO_JUMP = create(prefix(SCExaNames.ENHANCED_AUTO_JUMP));
    public static final IExtraAbility ESCAPER = create(prefix(SCExaNames.ESCAPER), 5);
    public static final IExtraAbility EXPLOSION_MASTER = create(prefix(SCExaNames.EXPLOSION_MASTER));
    public static final IExtraAbility FALLING_BUFFER = create(prefix(SCExaNames.FALLING_BUFFER));
    public static final IExtraAbility FANGS_SUMMONER = createSpecial(prefix(SCExaNames.FANGS_SUMMONER));
    public static final IExtraAbility FAVORED_TRADER = create(prefix(SCExaNames.FAVORED_TRADER));
    public static final IExtraAbility FIRE_RESISTANCE = create(prefix(SCExaNames.FIRE_RESISTANCE), 6);
    public static final IExtraAbility FROST_RESISTANCE = create(prefix(SCExaNames.FROST_RESISTANCE));
    public static final IExtraAbility GOLD_PREFERENCE = create(prefix(SCExaNames.GOLD_PREFERENCE));
    public static final IExtraAbility IMITATOR = create(prefix(SCExaNames.IMITATOR));
    public static final IExtraAbility INITIAL_ARMOR = create(prefix(SCExaNames.INITIAL_ARMOR));
    public static final IExtraAbility MONSTER_SABOTAGE = createSpecial(prefix(SCExaNames.MONSTER_SABOTAGE));
    public static final IExtraAbility MONSTER_VIEW = create(prefix(SCExaNames.MONSTER_VIEW));
    public static final IExtraAbility NETHERMAN = create(prefix(SCExaNames.NETHERMAN));
    public static final IExtraAbility NUTRITIONIST = create(prefix(SCExaNames.NUTRITIONIST), 5);
    public static final IExtraAbility OVERDRIVE = create(prefix(SCExaNames.OVERDRIVE));
    public static final IExtraAbility PERMANENT_SLOWDOWN = create(prefix(SCExaNames.PERMANENT_SLOWDOWN), 5);
    public static final IExtraAbility PILLAGER = create(prefix(SCExaNames.PILLAGER));
    public static final IExtraAbility POISONER = create(prefix(SCExaNames.POISONER));
    public static final IExtraAbility PURIFICATION = create(prefix(SCExaNames.PURIFICATION), 5);
    public static final IExtraAbility RESTORATION = create(prefix(SCExaNames.RESTORATION));
    public static final IExtraAbility SLIME_POWER = create(prefix(SCExaNames.SLIME_POWER), 5);
    public static final IExtraAbility SOUL_INVULNERABILITY = create(prefix(SCExaNames.SOUL_INVULNERABILITY));
    public static final IExtraAbility SPEEDUP = create(prefix(SCExaNames.SPEEDUP));
    public static final IExtraAbility STATIC_DEFENDER = create(prefix(SCExaNames.STATIC_DEFENDER), 5);
    public static final IExtraAbility SWIMMER = create(prefix(SCExaNames.SWIMMER), 6);
    public static final IExtraAbility TELEPORTATION = create(prefix(SCExaNames.TELEPORTATION), 5);
    public static final IExtraAbility THORNS_MASTER = create(prefix(SCExaNames.THORNS_MASTER));
    public static final IExtraAbility TRANSFORMATION = create(prefix(SCExaNames.TRANSFORMATION), 5);
    public static final IExtraAbility ULTRAREACH = create(prefix(SCExaNames.ULTRAREACH));
    public static final IExtraAbility WATER_BREATHING = create(prefix(SCExaNames.WATER_BREATHING), 5);
    public static final IExtraAbility WITHER_REACH = createSpecial(prefix(SCExaNames.WITHER_REACH));

    private static final Map<ResourceLocation, IExtraAbility> ABILITIES = new HashMap<>(64);
    private static final Map<EntityType<?>, IExtraAbility> ENTITY_TO_EXA_MAP = new HashMap<>(64);
    private static final int DEFAULT_COST = 4;
    @NotNull
    private final ResourceLocation registryName;
    private final int cost;
    private final boolean special;

    private ExtraAbility(ResourceLocation registryName, int cost, boolean special) {
        this.registryName = registryName;
        this.cost = cost;
        this.special = special;
    }

    static {
        register(ARMOR_PIERCER, ArmorPiercerBuff.INSTANCE, EntityType.VINDICATOR);
        register(BOW_EXPERT, EntityType.SKELETON);
        register(CHEMIST, EntityType.WITCH);
        register(CLIMBER, EntityType.SPIDER);
        register(DESTROYER, EntityType.WOLF);
        register(DRAGON_WIZARD, EntityType.ENDER_DRAGON);
        register(ENHANCED_AUTO_JUMP, EntityType.RABBIT);
        register(ESCAPER, EscaperBuff.INSTANCE, EntityType.SQUID);
        register(EXPLOSION_MASTER, ExplosionMasterBuff.INSTANCE, EntityType.CREEPER);
        register(FALLING_BUFFER, EntityType.CAT, EntityType.OCELOT, EntityType.CHICKEN, EntityType.GHAST);
        register(FANGS_SUMMONER, EntityType.EVOKER);
        register(FAVORED_TRADER, EntityType.WANDERING_TRADER);
        register(FIRE_RESISTANCE, FireResistanceBuff.INSTANCE, EntityType.BLAZE, EntityType.MAGMA_CUBE, EntityType.STRIDER);
        register(FROST_RESISTANCE, FrostResistanceBuff.INSTANCE, FrostResistanceDebuff.INSTANCE, EntityType.POLAR_BEAR);
        register(GOLD_PREFERENCE, GoldPreferenceBuff.INSTANCE, EntityType.PIGLIN);
        register(IMITATOR, EntityType.PARROT);
        register(INITIAL_ARMOR, InitialArmorBuff.INSTANCE, EntityType.ZOMBIE, EntityType.SILVERFISH);
        register(MONSTER_SABOTAGE, MonsterSabotageDebuff.INSTANCE, EntityType.ELDER_GUARDIAN, EntityType.RAVAGER);
        register(MONSTER_VIEW, MonsterViewBuff.INSTANCE, EntityType.BAT, EntityType.PHANTOM);
        register(NETHERMAN, NethermanBuff.INSTANCE, EntityType.HOGLIN, EntityType.ZOGLIN, EntityType.ZOMBIFIED_PIGLIN);
        register(NUTRITIONIST, EntityType.PIG);
        register(OVERDRIVE, EntityType.HUSK);
        register(PERMANENT_SLOWDOWN, PermanentSlowdownBuff.INSTANCE, EntityType.STRAY);
        register(PILLAGER, EntityType.PILLAGER);
        register(POISONER, PoisonerBuff.INSTANCE, EntityType.BEE, EntityType.CAVE_SPIDER);
        register(PURIFICATION, PurificationBuff.INSTANCE, EntityType.COW, EntityType.MOOSHROOM);
        register(RESTORATION, RestorationBuff.INSTANCE, EntityType.SHEEP);
        register(SLIME_POWER, SlimePowerBuff.INSTANCE, EntityType.SLIME);
        register(SOUL_INVULNERABILITY, ModEntities.SOUL_SKELETON, ModEntities.WANDERER);
        register(SPEEDUP, SpeedupBuff.INSTANCE, EntityType.HORSE, EntityType.DONKEY, EntityType.MULE, EntityType.LLAMA, EntityType.TRADER_LLAMA, EntityType.ZOMBIE_HORSE, EntityType.SKELETON_HORSE);
        register(STATIC_DEFENDER, StaticDefenderBuff.INSTANCE, EntityType.SHULKER, EntityType.TURTLE);
        register(SWIMMER, SwimmerBuff.INSTANCE, EntityType.DOLPHIN, EntityType.DROWNED);
        register(TELEPORTATION, EntityType.ENDERMAN);
        register(THORNS_MASTER, EntityType.GUARDIAN, EntityType.PUFFERFISH);
        register(TRANSFORMATION, TransformationBuff.INSTANCE, EntityType.WITHER_SKELETON);
        register(ULTRAREACH, UltrareachBuff.INSTANCE, EntityType.FOX);
        register(WATER_BREATHING, WaterBreathingBuff.INSTANCE, EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH);
        register(WITHER_REACH, WitherReachBuff.INSTANCE, EntityType.WITHER);
    }

    @Nullable
    public static IExtraAbility get(ResourceLocation registryName) {
        return ABILITIES.get(registryName);
    }

    public static Optional<IExtraAbility> getOptional(ResourceLocation registryName) {
        return Optional.ofNullable(ABILITIES.get(registryName));
    }

    @SuppressWarnings("unused")
    @Nullable
    public static IExtraAbility byEntity(EntityType<?> type) {
        return ENTITY_TO_EXA_MAP.get(type);
    }

    @SuppressWarnings("unused")
    @Nullable
    public static IExtraAbility byReinforcement(Reinforcement reinforcement) {
        return ENTITY_TO_EXA_MAP.get(reinforcement.getType());
    }

    public static IExtraAbility create(ResourceLocation registryName) {
        return create(registryName, DEFAULT_COST, false);
    }

    public static IExtraAbility createSpecial(ResourceLocation registryName) {
        return create(registryName, DEFAULT_COST, true);
    }

    public static IExtraAbility create(ResourceLocation registryName, int cost) {
        return create(registryName, cost, false);
    }

    public static IExtraAbility create(ResourceLocation registryName, int cost, boolean special) {
        return new ExtraAbility(registryName, cost, special);
    }

    public static void register(IExtraAbility exa) {
        Objects.requireNonNull(exa, "Extra Ability should be non-null");
        if (exa.isDummy()) {
            return;
        }
        Objects.requireNonNull(exa.getRegistryName(), "Registry name should be non-null");
        Preconditions.checkState(ABILITIES.put(exa.getRegistryName(), exa) == null, "Duplicate registry name: " + exa.getRegistryName());
    }

    public static void register(IExtraAbility exa, EntityType<?>... types) {
        register(exa);
        Arrays.stream(types).distinct().forEach(type -> ENTITY_TO_EXA_MAP.put(type, exa));
    }

    public static void register(IExtraAbility exa, PlayerBuff buff, EntityType<?>... types) {
        register(exa, types);
        PlayerBuffMap.bind(exa, buff);
    }

    public static void register(IExtraAbility exa, MobDebuff debuff, EntityType<?>... types) {
        register(exa, types);
        MobDebuffMap.bind(exa, debuff);
    }

    public static void register(IExtraAbility exa, PlayerBuff buff, MobDebuff debuff, EntityType<?>... types) {
        register(exa, types);
        PlayerBuffMap.bind(exa, buff);
        MobDebuffMap.bind(exa, debuff);
    }

    public static ImmutableMap<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities() {
        return ImmutableMap.copyOf(ABILITIES);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    @Override
    public boolean isOn(PlayerEntity player) {
        return ((IPlayerEntityMixin) player).hasExtraAbility(this);
    }

    @Override
    public boolean addTo(PlayerEntity player) {
        boolean added = ((IPlayerEntityMixin) player).addExtraAbility(this);
        if (added) {
            PlayerBuffMap.getBuff(this).ifPresent(buff -> buff.startApplyingTo(player, player.level));
            MobDebuffMap.getDebuff(this).ifPresent(debuff -> debuff.startApplyingTo(player, player.level));
        }
        return added;
    }

    @Override
    public boolean removeFrom(PlayerEntity player) {
        boolean removed = ((IPlayerEntityMixin) player).removeExtraAbility(this);
        if (removed) {
            PlayerBuffMap.getBuff(this).ifPresent(buff -> buff.stopApplyingTo(player, player.level));
            MobDebuffMap.getDebuff(this).ifPresent(debuff -> debuff.stopApplyingTo(player, player.level));
        }
        return removed;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(Util.makeDescriptionId("exa", getRegistryName()));
    }

    @Override
    public int getSoulContainerCost() {
        return cost;
    }

    @Override
    public int getSECost() {
        return getSoulContainerCost() * getSoulContainerCost() * 100 * (isSpecial() ? 10 : 1);
    }

    @Override
    public boolean isSpecial() {
        return special;
    }

    @Override
    public TextFormatting getStyle() {
        return isSpecial() ? TextFormatting.DARK_PURPLE : TextFormatting.BLUE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraAbility that = (ExtraAbility) o;
        return getRegistryName().equals(that.getRegistryName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRegistryName());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("registryName", registryName)
                .toString();
    }

    @Override
    public int compareTo(IExtraAbility o) {
        if (isSpecial() != o.isSpecial()) {
            return isSpecial() ? -1 : 1;
        }
        String s1 = I18n.get(getDisplayName().getString());
        String s2 = I18n.get(o.getDisplayName().getString());
        return s1.compareTo(s2);
    }
}
