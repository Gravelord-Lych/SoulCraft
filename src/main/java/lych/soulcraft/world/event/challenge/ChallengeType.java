package lych.soulcraft.world.event.challenge;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.world.event.manager.ChallengeManager;
import lych.soulcraft.world.event.manager.HuntingChallengeManager;
import lych.soulcraft.world.event.manager.SurvivalChallengeManager;
import lych.soulcraft.world.event.manager.UndeadSurvivalChallengeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;

/**
 * @author Gravelord Lych
 */
public final class ChallengeType<T extends Challenge> implements Comparable<ChallengeType<T>> {
    public static final ChallengeType<HuntingChallenge> HUNTING = new ChallengeType<>(SoulCraft.prefix("hunting"), HuntingChallengeManager::get);
    public static final ChallengeType<SurvivalChallenge> SURVIVAL = new ChallengeType<>(SoulCraft.prefix("survival"), SurvivalChallengeManager::get);
    public static final ChallengeType<UndeadSurvivalChallenge> UNDEAD_SURVIVAL = new ChallengeType<>(SoulCraft.prefix("undead_survival"), UndeadSurvivalChallengeManager::get);

    private static final Set<ChallengeType<?>> CHALLENGES = new TreeSet<>();

    static {
        registerChallenge(HUNTING);
        registerChallenge(SURVIVAL);
        registerChallenge(UNDEAD_SURVIVAL);
    }

    @NotNull
    private final ResourceLocation registryName;
    private final Function<? super ServerWorld, ? extends ChallengeManager<T>> challengeManagerGetter;
    private final int maxPlayerCount;
    private final double findPlayerDistance;
    @Nullable
    private String descriptionId;

    public ChallengeType(ResourceLocation registryName, Function<? super ServerWorld, ? extends ChallengeManager<T>> challengeManagerGetter) {
        this(registryName, challengeManagerGetter, ChallengeManager.FIND_CHALLENGE_DISTANCE);
    }

    public ChallengeType(ResourceLocation registryName, Function<? super ServerWorld, ? extends ChallengeManager<T>> challengeManagerGetter, double findPlayerDistance) {
        this(registryName, challengeManagerGetter, Integer.MAX_VALUE, findPlayerDistance);
    }

    public ChallengeType(ResourceLocation registryName, Function<? super ServerWorld, ? extends ChallengeManager<T>> challengeManagerGetter, int maxPlayerCount, double findPlayerDistance) {
        this.challengeManagerGetter = Objects.requireNonNull(challengeManagerGetter, "ChallengeManagerGetter should be non-null");
        Objects.requireNonNull(registryName, "RegistryName should be non-null");
        Preconditions.checkArgument(maxPlayerCount > 0, "MaxPlayerCount should be positive");
        Preconditions.checkArgument(findPlayerDistance > 0, "FindPlayerDistance should be positive");
        this.maxPlayerCount = maxPlayerCount;
        this.findPlayerDistance = findPlayerDistance;
        this.registryName = registryName;
    }

    public ChallengeManager<T> getManager(ServerWorld world) {
        return challengeManagerGetter.apply(world);
    }

    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public String getDescriptionId() {
        if (descriptionId == null) {
            descriptionId = Util.makeDescriptionId("challenge", getRegistryName());
        }
        return descriptionId;
    }

    public TranslationTextComponent getDisplayName() {
        return new TranslationTextComponent(getDescriptionId());
    }

    public static void registerChallenge(ChallengeType<?> challenge) {
        Objects.requireNonNull(challenge, "ChallengeType should be non-null");
        Preconditions.checkArgument(CHALLENGES.add(challenge), "Duplicate ChallengeType: " + challenge.getRegistryName());
    }

    public static ImmutableSet<ChallengeType<?>> getChallengeTypes() {
        return ImmutableSet.copyOf(CHALLENGES);
    }

    public static Optional<ChallengeType<?>> getOptional(ResourceLocation registryName) {
        for (ChallengeType<?> challenge : getChallengeTypes()) {
            if (challenge.getRegistryName().equals(registryName)) {
                return Optional.of(challenge);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChallengeType<?> that = (ChallengeType<?>) o;
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
                .add("maxPlayerCount", maxPlayerCount)
                .add("findPlayerDistance", findPlayerDistance)
                .toString();
    }

    @Override
    public int compareTo(@NotNull ChallengeType<T> o) {
        return getRegistryName().compareTo(o.getRegistryName());
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public double getFindPlayerDistance() {
        return findPlayerDistance;
    }
}
