package lych.soulcraft.world.event.challenge;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.entity.monster.SoulSkeletonEntity;
import lych.soulcraft.entity.monster.WandererEntity;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.world.event.manager.UnknownObjectException;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

public class SurvivalChallenge extends Challenge {
    private static final Supplier<EffectInstance> GLOWING_EFFECT_SUPPLIER = () -> new EffectInstance(Effects.GLOWING, 20 * 5);
    private final Spawner<SoulSkeletonEntity> spawner01 = new Spawner<>(ModEntities.SOUL_SKELETON, 3, 4, 200, 300, 4, 26);
    private final Spawner<SoulSkeletonEntity> spawner02 = new Spawner<>(ModEntities.SOUL_SKELETON, 1, 2, 80, 160, 2, 28);
    private final Spawner<WandererEntity> spawner03 = new Spawner<>(ModEntities.WANDERER, 1, 1, 100, 200, 8, 22);
    private ChallengeEvent exMobSpawnEvent;
    private final Object2FloatMap<UUID> totalDamageMap = new Object2FloatOpenHashMap<>();

    public SurvivalChallenge(int id, ServerWorld level, BlockPos center) {
        this(ChallengeType.SURVIVAL, id, level, center);
    }

    protected SurvivalChallenge(ChallengeType<? extends SurvivalChallenge> type, int id, ServerWorld level, BlockPos center) {
        super(type, id, level, center);
    }

    public SurvivalChallenge(ServerWorld level, CompoundNBT compoundNBT) throws UnknownObjectException {
        super(level, compoundNBT);
        if (compoundNBT.contains("TotalDamageMap", Constants.NBT.TAG_LIST)) {
            totalDamageMap.clear();
            ListNBT listNBT = compoundNBT.getList("TotalDamageMap", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT damageNBT = listNBT.getCompound(i);
                UUID uuid = damageNBT.getUUID("ChallengerUUID");
                float damage = damageNBT.getFloat("Damage");
                totalDamageMap.put(uuid, damage);
            }
        }
    }

    @Override
    protected void initEvents() {
        exMobSpawnEvent = new ExtraMobSpawnEvent("ExtraMobSpawn", 500, 750, 40, true, GLOWING_EFFECT_SUPPLIER, Pair.of(ModEntities.SOUL_SKELETON, 4), Pair.of(ModEntities.WANDERER, 2));
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        ListNBT listNBT = new ListNBT();
        for (UUID uuid : totalDamageMap.keySet()) {
            CompoundNBT damageNBT = new CompoundNBT();
            damageNBT.putUUID("ChallengerUUID", uuid);
            damageNBT.putFloat("Damage", totalDamageMap.getFloat(uuid));
            listNBT.add(damageNBT);
        }
        compoundNBT.put("TotalDamageMap", listNBT);
    }

    @Override
    public IFormattableTextComponent getChallengeText() {
        return getDirectChallengeText().copy().append(" - ").append(timeRemainingText());
    }

    @Override
    protected int getTimeLimit() {
        return 20 * 300;
    }

    @Override
    protected boolean hasProgress() {
        return false;
    }

    @Override
    protected void onVictory() {}

    @Override
    protected float getProgress() {
        return 0;
    }

    @Nullable
    @Override
    protected ChallengeMedalType getMedalTypeForFinishing(ServerPlayerEntity player) {
        if (timeRemaining > 0) {
            return null;
        }
        float damage = totalDamageMap.getOrDefault(player.getUUID(), 0);
        if (damage <= getDiamondDamage()) {
            return getDifficulty() == Difficulty.HARD && isStrict() ? ChallengeMedalType.NETHERITE : ChallengeMedalType.DIAMOND;
        }
        if (damage <= getGoldDamage()) {
            return ChallengeMedalType.GOLD;
        }
        return ChallengeMedalType.IRON;
    }

    protected float getDiamondDamage() {
        return 20;
    }

    protected float getGoldDamage() {
        return 50;
    }

    public void addDamageFor(ServerPlayerEntity player, float damage) {
        addDamageFor(player.getUUID(), damage);
    }

    public void addDamageFor(UUID uuid, float damage) {
        totalDamageMap.put(uuid, totalDamageMap.getOrDefault(uuid, 0) + damage);
    }

    @Override
    protected Iterable<BaseSpawner<?>> getSpawners() {
        return ImmutableList.of(spawner01, spawner02, spawner03);
    }

    @Override
    protected Iterable<ChallengeEvent> getEvents() {
        return ImmutableList.of(exMobSpawnEvent);
    }

    protected class TimeLimitedSpawner<T extends MobEntity> extends Spawner<T> {
        private final int start;
        private final int stop;

        public TimeLimitedSpawner(EntityType<T> spawnType, int minSpawnCount, int maxSpawnCount, int minSpawnInterval, int maxSpawnInterval, int start, int stop, double minSpawnDistance, double spawnDistanceScale) {
            super(spawnType, minSpawnCount, maxSpawnCount, minSpawnInterval, maxSpawnInterval, minSpawnDistance, spawnDistanceScale);
            this.start = start;
            this.stop = stop;
        }

        @Override
        public void tick() {
            if (timeRemaining >= stop && timeRemaining < start) {
                super.tick();
            }
        }
    }

    protected class TimeFixedSpawner<T extends MobEntity> extends Spawner<T> {
        private final int[] fixedTime;

        public TimeFixedSpawner(EntityType<T> spawnType, int minSpawnCount, int maxSpawnCount, double minSpawnDistance, double spawnDistanceScale, int... fixedTime) {
            super(spawnType, minSpawnCount, maxSpawnCount, 1, 1, minSpawnDistance, spawnDistanceScale);
            this.fixedTime = fixedTime;
        }

        @Override
        public void tick() {
            if (Arrays.stream(fixedTime).allMatch(time -> time == timeRemaining)) {
                spawn();
            }
        }
    }

    protected class Spawner<T extends MobEntity> extends BaseSpawner<T> {
        public Spawner(EntityType<T> spawnType, int minSpawnCount, int maxSpawnCount, int minSpawnInterval, int maxSpawnInterval, double minSpawnDistance, double spawnDistanceScale) {
            super(spawnType, minSpawnCount, maxSpawnCount, minSpawnInterval, maxSpawnInterval, minSpawnDistance, spawnDistanceScale);
        }

        @Override
        protected void onMobSpawn(T mob) {
            mob.addEffect(GLOWING_EFFECT_SUPPLIER.get());
            EntityUtils.spawnAnimServerside(mob, level);
            EntityUtils.addParticlesAroundSelfServerside(mob, level, ParticleTypes.SOUL_FIRE_FLAME, 5 + random.nextInt(3));
        }

        @Override
        protected int getMobCountLimit() {
            return 25;
        }
    }
}
