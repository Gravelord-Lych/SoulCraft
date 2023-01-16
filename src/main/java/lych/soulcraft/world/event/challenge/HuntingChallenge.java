package lych.soulcraft.world.event.challenge;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.entity.monster.SoulSkeletonEntity;
import lych.soulcraft.entity.monster.WandererEntity;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.world.event.manager.UnknownObjectException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class HuntingChallenge extends Challenge {
    private static final Supplier<EffectInstance> GLOWING_EFFECT_SUPPLIER = () -> new EffectInstance(Effects.GLOWING, Integer.MAX_VALUE);
    private final Spawner<SoulSkeletonEntity> spawner01 = new Spawner<>(ModEntities.SOUL_SKELETON, 2, 3, 300, 450, 4, 26);
    private final Spawner<SoulSkeletonEntity> spawner02 = new Spawner<>(ModEntities.SOUL_SKELETON, 1, 2, 120, 200, 2, 28);
    private final Spawner<WandererEntity> spawner03 = new Spawner<>(ModEntities.WANDERER, 1, 1, 150, 300, 8, 22);
    private ChallengeEvent fireballEvent;
    private ChallengeEvent exMobSpawnEvent;
    private final int maxKillCount = maxKillCount();
    private int killCount;

    public HuntingChallenge(int id, ServerWorld level, BlockPos center) {
        super(ChallengeType.HUNTING, id, level, center);
    }

    public HuntingChallenge(ServerWorld level, CompoundNBT compoundNBT) throws UnknownObjectException {
        super(level, compoundNBT);
    }

    @Override
    protected void initEvents() {
        fireballEvent = new FireballDropEvent("FireballDrop", 300, 500, 40, 2, EntityType.FIREBALL);
        exMobSpawnEvent = new ExtraMobSpawnEvent("ExtraMobSpawn", 750, 1500, 40, true, GLOWING_EFFECT_SUPPLIER,
                Pair.of(ModEntities.SOUL_SKELETON, 3), Pair.of(ModEntities.WANDERER, 2));
    }

    @Override
    public IFormattableTextComponent getChallengeText() {
        return getDirectChallengeText().copy().append(" - ").append(timeRemainingText()).append(COMMA).append(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "killed"), killCount, maxKillCount));
    }

    @Override
    protected void onVictory() {}

    protected int maxKillCount() {
        return 50;
    }

    public int getMaxKillCount() {
        return maxKillCount;
    }

    @Override
    public void onChallengeMobDeath(DamageSource source, Entity deadEntity) {
        super.onChallengeMobDeath(source, deadEntity);
        if (deadEntity instanceof MobEntity) {
            MobEntity mob = (MobEntity) deadEntity;
            if (source.getEntity() instanceof PlayerEntity) {
                handlePlayerKiller(source.getEntity());
            } else if (mob.getKillCredit() instanceof PlayerEntity) {
                handlePlayerKiller(mob.getKillCredit());
            }
        }
    }

    private void handlePlayerKiller(Entity source) {
        PlayerEntity player = (PlayerEntity) source;
        if (challengerUUIDs.contains(player.getUUID()) && killCount < maxKillCount) {
            killCount++;
        }
    }

    @Override
    protected int getTimeLimit() {
        return 20 * 300;
    }

    @Override
    protected float getProgress() {
        return MathHelper.clamp((float) killCount / maxKillCount, 0, 1);
    }

    @Nullable
    @Override
    protected ChallengeMedalType getMedalTypeForFinishing(ServerPlayerEntity player) {
        if (killCount < maxKillCount) {
            return null;
        }
        if (timeRemaining >= getTimeLimit() * 0.6f) {
            return getDifficulty() == Difficulty.HARD && isStrict() ? ChallengeMedalType.NETHERITE : ChallengeMedalType.DIAMOND;
        }
        if (timeRemaining >= getTimeLimit() * 0.25f) {
            return ChallengeMedalType.GOLD;
        }
        return ChallengeMedalType.IRON;
    }

    @Override
    protected Iterable<BaseSpawner<?>> getSpawners() {
        return ImmutableList.of(spawner01, spawner02, spawner03);
    }

    @Override
    protected Iterable<ChallengeEvent> getEvents() {
        return ImmutableList.of(fireballEvent, exMobSpawnEvent);
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
    }
}
