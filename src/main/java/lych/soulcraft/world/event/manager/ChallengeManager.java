package lych.soulcraft.world.event.manager;

import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ChallengeManager<T extends Challenge> extends EventManager<T> {
    public static final double FIND_CHALLENGE_DISTANCE = 48;
    private final ChallengeType<? extends T> type;
    private final ChallengeMaker<? extends T> challengeMaker;
    private final ChallengeLoader<? extends T> challengeLoader;

    public ChallengeManager(String id, ServerWorld level, ChallengeType<? extends T> type, ChallengeMaker<? extends T> challengeMaker, ChallengeLoader<? extends T> challengeLoader) {
        super(id, level);
        this.type = type;
        this.challengeMaker = challengeMaker;
        this.challengeLoader = challengeLoader;
    }

    protected static boolean validPlayer(@Nullable ServerPlayerEntity player) {
        return player != null && !player.isSpectator() && player.isAlive();
    }

    @Override
    protected T loadEvent(ServerWorld world, CompoundNBT compoundNBT) throws UnknownObjectException {
        return challengeLoader.load(world, compoundNBT);
    }

    public Collection<T> getChallenges() {
        return eventMap.values();
    }

    public T createChallengeFor(Set<ServerPlayerEntity> players, ServerWorld world, BlockPos center) {
        if (players.stream().noneMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("No challengers found");
        }
        T challenge = getOrCreateChallenge(world, center);
        if (!challenge.isStarted()) {
            eventMap.putIfAbsent(challenge.getId(), challenge);
        }
        players.stream()
                .distinct()
                .filter(ChallengeManager::validPlayer)
                .filter(player -> player.distanceToSqr(Vector3d.atCenterOf(center)) <= type.getFindPlayerDistance() * type.getFindPlayerDistance())
                .sorted(Comparator.comparingDouble(player -> player.distanceToSqr(Vector3d.atCenterOf(center))))
                .limit(type.getMaxPlayerCount())
                .collect(Collectors.toSet())
                .forEach(challenge::addChallenger);
        setDirty();
        return challenge;
    }

    public static List<ChallengeManager<?>> all(ServerWorld world) {
        return ChallengeType.getChallengeTypes().stream().map(type -> byType(type, world)).collect(Collectors.toList());
    }

    public static <T extends Challenge> ChallengeManager<T> byType(ChallengeType<T> type, ServerWorld world) {
        return type.getManager(world);
    }

    public T getOrCreateChallenge(ServerWorld world, BlockPos center) {
        T challenge = getNearbyEvent(center, FIND_CHALLENGE_DISTANCE);
        return challenge == null ? challengeMaker.make(getUniqueID(), world, center) : challenge;
    }

    @FunctionalInterface
    public interface ChallengeMaker<T> {
        T make(int id, ServerWorld level, BlockPos center);
    }

    @FunctionalInterface
    public interface ChallengeLoader<T> {
        T load(ServerWorld level, CompoundNBT compoundNBT) throws UnknownObjectException;
    }
}
