package lych.soulcraft.world.event.challenge;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.world.event.manager.UnknownObjectException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class UndeadSurvivalChallenge extends SurvivalChallenge {
    private static final int SKELETON_LIMIT = 20 * 500;
    private static final int WITHER_SKELETON_LIMIT = 20 * 400;
    private static final int EXTRA_WITHER_SKELETON_LIMIT = 20 * 300;
    private static final int WITHER_LIMIT = 20 * 200;

    private final Spawner<WitherSkeletonEntity> exWitherSkeletonSpawner = new TimeLimitedSpawner<>(EntityType.WITHER_SKELETON, 3, 4, 240, 400, EXTRA_WITHER_SKELETON_LIMIT, WITHER_LIMIT, 5, 25);
    private final Spawner<HuskEntity> huskSpawner = new HuskSpawner(2, 3, 240, 600, 3, 27);
    private final Spawner<SkeletonEntity> skeletonSpawner = new TimeLimitedSpawner<>(EntityType.SKELETON, 1, 2, 180, 340, SKELETON_LIMIT, EXTRA_WITHER_SKELETON_LIMIT, 8, 22);
    private final Spawner<WitchEntity> witchSpawner = new TimeLimitedSpawner<>(EntityType.WITCH, 1, 1, 320, 550, WITHER_SKELETON_LIMIT, WITHER_LIMIT, 8, 22);
    private final Spawner<WitherSkeletonEntity> witherSkeletonSpawner = new TimeLimitedSpawner<>(EntityType.WITHER_SKELETON, 1, 2, 160, 320, WITHER_SKELETON_LIMIT, 0, 3, 27);
    private final Spawner<WitherEntity> witherSpawner = new WitherSpawner(1, 1, 10, 20, WITHER_LIMIT);
    private final Spawner<ZombieEntity> zombieSpawner = new Spawner<>(EntityType.ZOMBIE, 2, 3, 160, 400, 3, 27);
    @Nullable
    private WitherEntity witherInTheChallenge;

    public UndeadSurvivalChallenge(int id, ServerWorld level, BlockPos center) {
        super(ChallengeType.UNDEAD_SURVIVAL, id, level, center);
    }

    public UndeadSurvivalChallenge(ServerWorld level, CompoundNBT compoundNBT) throws UnknownObjectException {
        super(level, compoundNBT);
    }

    @Nullable
    @Override
    protected ChallengeMedalType getMedalTypeForFinishing(ServerPlayerEntity player) {
        if (EntityUtils.isAlive(witherInTheChallenge)) {
            return null;
        }
        return super.getMedalTypeForFinishing(player);
    }

    @Override
    protected float getDiamondDamage() {
        return super.getDiamondDamage() * 1.5f;
    }

    @Override
    protected float getGoldDamage() {
        return super.getGoldDamage() * 1.5f;
    }

    @Override
    protected int getTimeLimit() {
        return 20 * 600;
    }

    @Override
    protected Iterable<BaseSpawner<?>> getSpawners() {
        return ImmutableList.of(exWitherSkeletonSpawner, huskSpawner, skeletonSpawner, witchSpawner, witherSkeletonSpawner, witherSpawner, zombieSpawner);
    }

    @Override
    protected Iterable<ChallengeEvent> getEvents() {
        return ImmutableList.of();
    }

    private class WitherSpawner extends TimeFixedSpawner<WitherEntity> {
        public WitherSpawner(int minSpawnCount, int maxSpawnCount, double minSpawnDistance, double spawnDistanceScale, int... fixedTime) {
            super(EntityType.WITHER, minSpawnCount, maxSpawnCount, minSpawnDistance, spawnDistanceScale, fixedTime);
        }

        @Override
        protected void onMobSpawn(WitherEntity wither) {
            super.onMobSpawn(wither);
            witherInTheChallenge = wither;
        }

        @Override
        protected boolean shouldSpawn() {
            return super.shouldSpawn() && witherInTheChallenge == null;
        }
    }

    private class HuskSpawner extends Spawner<HuskEntity> {
        private final Difficulty[] availableDifficulties = new Difficulty[]{Difficulty.NORMAL, Difficulty.HARD};

        public HuskSpawner(int minSpawnCount, int maxSpawnCount, int minSpawnInterval, int maxSpawnInterval, double minSpawnDistance, double spawnDistanceScale) {
            super(EntityType.HUSK, minSpawnCount, maxSpawnCount, minSpawnInterval, maxSpawnInterval, minSpawnDistance, spawnDistanceScale);
        }

        @Override
        protected Difficulty[] getAvailableDifficulties() {
            return availableDifficulties;
        }
    }
}
