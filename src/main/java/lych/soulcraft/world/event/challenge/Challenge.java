package lych.soulcraft.world.event.challenge;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.capability.NonAPICapabilities;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.util.*;
import lych.soulcraft.world.IChallengeTimeTextComponent;
import lych.soulcraft.world.event.AbstractWorldEvent;
import lych.soulcraft.world.event.manager.ChallengeManager;
import lych.soulcraft.world.event.manager.UnknownObjectException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The superclass of all challenges.
 * @author Gravelord Lych
 */
public abstract class Challenge extends AbstractWorldEvent {
    public static final LoseReason EMPTY = new LoseReason();
    public static final LoseReason CHEAT = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.cheat")));
    public static final LoseReason CHEATED = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.cheated")));
    public static final LoseReason DIFFICULTY = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.difficulty")));
    public static final LoseReason DISTANCE = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.distance")));
    public static final LoseReason GAME_MODE = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.game_mode")));
    public static final LoseReason IMPLICATED = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.implicated")));
    public static final LoseReason UNREASONABLE_ITEM = new LoseReason(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason.unreasonable_item")));

    protected static final Logger LOGGER = LogManager.getLogger("challenge");
    protected static final double WARN_DISTANCE_OFFSET = -15;
    protected static final ITextComponent COMMA = new TranslationTextComponent(SoulCraft.prefixMsg("comma"));
    protected static final ITextComponent VICTORY = new TranslationTextComponent("event.minecraft.raid.victory");
    protected static final ITextComponent DEFEAT = new TranslationTextComponent("event.minecraft.raid.defeat");
    protected static final ITextComponent DISTANCE_WARNING = new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "warning.far_from_center"));

    protected final Set<UUID> challengerUUIDs = new HashSet<>();
    @NotNull
    private Status status;
    protected final ChallengeInfo challengeInfo;
    protected final ChallengeType<?> type;
    private final Object2LongMap<UUID> warnedPlayerMap = new Object2LongOpenHashMap<>();
    private final Difficulty difficulty;
    private final boolean strict;
    private final boolean completedInHardcore;
    private int preChallengeTicks;
    private int challengeEndTicks;
    private boolean canTick;
    protected boolean started;
    protected int timeRemaining;

    protected Challenge(ChallengeType<?> type, int id, ServerWorld level, BlockPos center) {
        super(id, level, center);
        canTick = false;
        this.type = type;
        status = Status.ONGOING;
        strict = ConfigHelper.strictChallengesEnabled();
        difficulty = level.getDifficulty();
        completedInHardcore = level.getLevelData().isHardcore();
        challengeInfo = initChallengeInfo();
        challengeInfo.getTimeInfo().setPercent(0);
        if (hasProgress()) {
            challengeInfo.getProgressInfo().setPercent(MathHelper.clamp(getInitProgress(), 0, 1));
        }
        preChallengeTicks = getMaxPreChallengeTicks();
        timeRemaining = getTimeLimit();
        initEvents();
        canTick = true;
    }

    protected Challenge(ServerWorld level, CompoundNBT compoundNBT) throws UnknownObjectException {
        super(level, compoundNBT);
        canTick = false;
        LOGGER.info("Loading challenge");
        ResourceLocation name = new ResourceLocation(compoundNBT.getString("ChallengeType"));
        this.type = ChallengeType.getOptional(name).orElseThrow(() -> new UnknownObjectException(name));
        status = Status.byName(compoundNBT.getString("Status"));
        started = compoundNBT.getBoolean("Started");
        strict = compoundNBT.getBoolean("Strict");
        difficulty = Difficulty.byId(compoundNBT.getInt("Difficulty"));
        completedInHardcore = compoundNBT.getBoolean("CompletedInHardCore");
        preChallengeTicks = compoundNBT.getInt("PreChallengeTicks");
        challengeEndTicks = compoundNBT.getInt("ChallengeEndTicks");
        timeRemaining = compoundNBT.getInt("TimeRemaining");
        challengeInfo = initChallengeInfo();
        challengerUUIDs.clear();
        if (compoundNBT.contains("Challengers", Constants.NBT.TAG_LIST)) {
            ListNBT challengerNBT = compoundNBT.getList("Challengers", Constants.NBT.TAG_INT_ARRAY);
            for (INBT nbt : challengerNBT) {
                challengerUUIDs.add(NBTUtil.loadUUID(nbt));
            }
        }
        initEvents();
        LOGGER.info("Challengers: " + challengersToString());
        if (compoundNBT.contains("ChallengeEvents", Constants.NBT.TAG_LIST)) {
            ListNBT challengeEvents = compoundNBT.getList("ChallengeEvents", Constants.NBT.TAG_COMPOUND);
            for (ChallengeEvent event : getEvents()) {
                CompoundNBT eventNBT = event.findNBTFrom(challengeEvents);
                event.load(eventNBT);
            }
        }
        canTick = true;
    }

    protected abstract void initEvents();

    protected ChallengeInfo initChallengeInfo() {
        return getCustomChallengeInfo() == null ?
                new ChallengeInfo(
                        new ServerBossInfo(
                                IChallengeTimeTextComponent.of(this::hasProgress, getChallengeText()),
                                BossInfo.Color.GREEN,
                                BossInfo.Overlay.PROGRESS),
                        new ServerBossInfo(
                                emptyTextComponent(),
                                BossInfo.Color.BLUE,
                                BossInfo.Overlay.PROGRESS))
                : getCustomChallengeInfo();
    }

    private static ITextComponent emptyTextComponent() {
        return new StringTextComponent("");
    }

    public boolean harderThan(Difficulty difficulty) {
        return harder(difficulty, getDifficulty());
    }

    private static boolean harder(Difficulty currentDifficulty, Difficulty newDifficulty) {
        return newDifficulty.getId() > currentDifficulty.getId();
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {
        super.addAdditionalSaveData(compoundNBT);
        LOGGER.info("Saving challenge");
        compoundNBT.putString("Status", status.name());
        compoundNBT.putString("ChallengeType", type.getRegistryName().toString());
        compoundNBT.putBoolean("Started", started);
        compoundNBT.putBoolean("Strict", strict);
        compoundNBT.putInt("Difficulty", difficulty.getId());
        compoundNBT.putBoolean("CompletedInHardCore", completedInHardcore);
        compoundNBT.putInt("PreChallengeTicks", preChallengeTicks);
        compoundNBT.putInt("ChallengeEndTicks", challengeEndTicks);
        compoundNBT.putInt("TimeRemaining", timeRemaining);
        ListNBT challengerUUIDNBT = new ListNBT();
        for (UUID uuid : challengerUUIDs) {
            challengerUUIDNBT.add(NBTUtil.createUUID(uuid));
        }
        compoundNBT.put("Challengers", challengerUUIDNBT);
        LOGGER.info("Challengers: " + challengersToString());
        ListNBT challengeEventNBT = new ListNBT();
        for (ChallengeEvent event : getEvents()) {
            challengeEventNBT.add(event.save());
        }
        compoundNBT.put("ChallengeEvents", challengeEventNBT);
    }

    public static <T extends Challenge> T createFor(ChallengeType<? extends T> type, ServerWorld world, BlockPos center, ServerPlayerEntity... challengers) {
        if (Arrays.stream(challengers).noneMatch(Objects::nonNull)) {
            throw new IllegalArgumentException("No challengers found");
        }
        return ChallengeManager.byType(type, world).createChallengeFor(new HashSet<>(Arrays.asList(challengers)), world, center);
    }

    private static boolean unreasonable(ItemStack stack) {
        return stack.getItem().getRegistryName() != null && Stream.of("minecraft", SoulCraft.MOD_ID).noneMatch(stack.getItem().getRegistryName().getNamespace()::equals);
    }

    public ITextComponent getDirectChallengeText() {
        return getType().getDisplayName();
    }

    public abstract IFormattableTextComponent getChallengeText();

    protected TranslationTextComponent timeRemainingText() {
        return new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "time_remaining"), timeRemaining / 20, getTimeLimit() / 20);
    }

    public Set<UUID> getChallengerUUIDs() {
        return challengerUUIDs;
    }

    public Set<ServerPlayerEntity> getChallengers() {
        return level.players().stream().filter(player -> challengerUUIDs.contains(player.getUUID())).collect(Collectors.toSet());
    }

    public int getChallengerCount() {
        return challengerUUIDs.size();
    }

    public boolean addChallenger(ServerPlayerEntity challenger) {
        if (challengerUUIDs.add(challenger.getUUID())) {
            addToInfo(challenger);
            return true;
        }
        return false;
    }

    protected void addToInfo(ServerPlayerEntity challenger) {
        challengeInfo.getTimeInfo().addPlayer(challenger);
        if (hasProgress()) {
            challengeInfo.getProgressInfo().addPlayer(challenger);
        }
    }

    public boolean removeChallenger(ServerPlayerEntity challenger) {
        if (challengerUUIDs.remove(challenger.getUUID())) {
            challengeInfo.getTimeInfo().removePlayer(challenger);
            if (hasProgress()) {
                challengeInfo.getProgressInfo().removePlayer(challenger);
            }
            return true;
        }
        return false;
    }

    public void removeAllChallengers() {
        challengerUUIDs.clear();
    }

    protected boolean hasProgress() {
        return true;
    }

    protected float getInitProgress() {
        return 0;
    }

    protected int getMaxPreChallengeTicks() {
        return 300;
    }

    protected int getMaxChallengeEndTicks() {
        return 600;
    }

    @Override
    protected void eventTick() {
        if (!canTick) {
            return;
        }
        super.eventTick();
        updatePlayers(challengeInfo.getTimeInfo());
        if (hasProgress()) {
            updatePlayers(challengeInfo.getProgressInfo());
        }
        if (getChallengerUUIDs().isEmpty()) {
            stop();
            return;
        }
        for (ServerPlayerEntity player : getChallengers()) {
            if (warnedPlayerMap.getOrDefault(player.getUUID(), -1) < ticksActive && player.distanceToSqr(Vector3d.atCenterOf(center)) >= Math.pow(getType().getFindPlayerDistance() + WARN_DISTANCE_OFFSET, 2)) {
                player.sendMessage(DISTANCE_WARNING.copy().withStyle(TextFormatting.RED), Util.NIL_UUID);
                warnedPlayerMap.put(player.getUUID(), ticksActive + 120);
            }
        }
        if (preChallengeTicks > 0) {
            preChallengeTicks--;
            onChallengePreparing(preChallengeTicks);
        } else {
            if (!started) {
                started = true;
                start();
            }
            if (timeRemaining > 0) {
                timeRemaining--;
                if (getChallengers().stream().allMatch(this::finishedChallenge)) {
                    win();
                    return;
                }
                if (getLoseReason() != null) {
                    lose(getLoseReason().getFirst(), getLoseReason().getSecond() == null ? getLoseReason().getFirst() : IMPLICATED, getLoseReason().getSecond());
                    return;
                }
            } else {
                if (getChallengers().stream().allMatch(this::finishedChallenge)) {
                    win();
                } else {
                    lose();
                }
                return;
            }
        }
        if (isStarted()) {
            getSpawners().forEach(BaseSpawner::tick);
            getEvents().forEach(ChallengeEvent::tick);
        }
        float timePercent = preChallengeTicks > 0 ? (float) (getMaxPreChallengeTicks() - preChallengeTicks) / getMaxPreChallengeTicks() : (float) timeRemaining / getTimeLimit();
        updateInfo(MathHelper.clamp(timePercent, 0, 1));
        setDirty();
        for (MobEntity mob : getChallengeMobs()) {
            if (mob.getTarget() == null || !(mob.getTarget() instanceof ServerPlayerEntity) || !getChallengers().contains((ServerPlayerEntity) mob.getTarget())) {
                @Nullable ServerPlayerEntity player = CollectionUtils.getRandom(getChallengers().stream().filter(playerIn -> !playerIn.abilities.invulnerable).collect(Collectors.toSet()), random);
                if (player != null) {
                    mob.setTarget(player);
                }
            }
        }
    }

    @Override
    protected void postEventTick() {
        super.postEventTick();
        updateInfo((float) timeRemaining / getTimeLimit());
        updatePlayers(challengeInfo.getTimeInfo());
        if (hasProgress()) {
            updatePlayers(challengeInfo.getProgressInfo());
        }
        setDirty();
        if (challengeEndTicks < getMaxChallengeEndTicks()) {
            challengeEndTicks++;
        } else {
            stop();
        }
    }

    protected void updatePlayers(ServerBossInfo bossInfo) {
        Set<ServerPlayerEntity> existPlayers = new HashSet<>(bossInfo.getPlayers());
        List<ServerPlayerEntity> players = level.getPlayers(validPlayer());

        for (ServerPlayerEntity player : players) {
            if (!existPlayers.contains(player)) {
                bossInfo.addPlayer(player);
            }
        }

        for (ServerPlayerEntity player : existPlayers) {
            if (!players.contains(player)) {
                bossInfo.removePlayer(player);
            }
        }
    }

    protected Predicate<ServerPlayerEntity> validPlayer() {
        return player -> player.isAlive() && getChallengerUUIDs().contains(player.getUUID());
    }

    protected void onChallengePreparing(int preChallengeTicks) {}

    protected void start() {
        if (isStrict()) {
            selfCheck();
            groundZero();
        }
    }

    protected void selfCheck() {
        if (getChallengers().stream().anyMatch(challenger -> challenger.gameMode.getGameModeForPlayer() == GameType.CREATIVE || challenger.gameMode.getGameModeForPlayer() == GameType.SPECTATOR)) {
            lose(GAME_MODE, GAME_MODE, null);
        }
    }

    /**
     * Any other conditions, see {@link lych.soulcraft.listener.CommonEventListener EventListener}
     * @return Non-null if the challenge should be lost.
     */
    @Nullable
    protected Pair<LoseReason, ServerPlayerEntity> getLoseReason() {
        AtomicReference<ServerPlayerEntity> playerRef = new AtomicReference<>();
        if (getChallengers().stream().anyMatch(player -> {
            if (player.getLevel().dimension() != level.dimension()) {
                playerRef.set(player);
                return true;
            }
            return false;
        })) {
            return Pair.of(DISTANCE, playerRef.get());
        }
        if (getChallengers().stream().anyMatch(player -> {
            if (player.distanceToSqr(Vector3d.atCenterOf(getCenter())) > type.getFindPlayerDistance() * type.getFindPlayerDistance()) {
                playerRef.set(player);
                return true;
            }
            return false;
        })) {
            return Pair.of(DISTANCE, playerRef.get());
        }
        if (isStrict()) {
//          Of course, Non-vanilla and non-soulcraft items are not allowed for strict challenges.
            if (getChallengers().stream().anyMatch(player -> {
                if (InventoryUtils.anyMatch(player.inventory, Challenge::unreasonable)) {
                    playerRef.set(player);
                    return true;
                }
                return false;
            })) {
                return Pair.of(UNREASONABLE_ITEM, playerRef.get());
            }
//          "Harder than the world difficulty" means a player has changed the world difficulty to make the challenge easier.
//          However, that won't work for strict challenges!!!!!
            if (harderThan(level.getDifficulty())) {
                return Pair.of(DIFFICULTY, null);
            }
//          Strict challenges should be completed without cheats.
            if (Utils.allowsCommands(level)) {
                return Pair.of(CHEAT, null);
            }
//          Players may want to turn cheats on first and then turn them off. That doesn't work!
            if (Utils.allowedCommands(level)) {
                return Pair.of(CHEATED, null);
            }
        }
        return null;
    }

    protected void updateInfo(float timePercent) {
        if (isOver()) {
            if (isVictory()) {
                challengeInfo.getTimeInfo().setName(IChallengeTimeTextComponent.of(this::hasProgress, getDirectChallengeText().copy().append(" - ").append(VICTORY)));
            } else {
                challengeInfo.getTimeInfo().setName(IChallengeTimeTextComponent.of(this::hasProgress, getDirectChallengeText().copy().append(" - ").append(DEFEAT)));
            }
        } else {
            challengeInfo.getTimeInfo().setName(IChallengeTimeTextComponent.of(this::hasProgress, getChallengeText()));
        }
        challengeInfo.getTimeInfo().setPercent(MathHelper.clamp(timePercent, 0, 1));
        challengeInfo.getTimeInfo().setColor(getColorForPercent(timePercent));
        challengeInfo.getProgressInfo().setPercent(getProgress());
    }

    protected BossInfo.Color getColorForPercent(float percent) {
        if (percent >= 0.6f) {
            return BossInfo.Color.GREEN;
        }
        if (percent >= 0.25f) {
            return BossInfo.Color.YELLOW;
        }
        return BossInfo.Color.RED;
    }

    public void win() {
        if (!isOver()) {
            status = Status.VICTORY;
            onVictory();
            getChallengeMobs().forEach(EntityUtils::killCompletely);
            LOGGER.info("Victory! Challengers: " + challengersToString());
            getChallengers().forEach(this::dropRewards);
            setDirty();
        }
    }

    protected List<String> challengersToString() {
        return getChallengers().stream().map(EntityUtils::nameAndUUIDToString).collect(Collectors.toList());
    }

    public void lose() {
        lose(EMPTY, EMPTY, null);
    }

    public void lose(LoseReason reason, LoseReason reasonForImplicatedPlayers, @Nullable ServerPlayerEntity cause) {
        if (!isOver()) {
            status = Status.LOSS;
            if (reason.hasDetailText()) {
                getChallengers().forEach(player -> {
                    if (reason.hasDetailText()) {
                        if (player.equals(cause)) {
                            player.sendMessage(reason.getDetailText(), Util.NIL_UUID);
                        } else {
                            player.sendMessage(reasonForImplicatedPlayers.getDetailText(), Util.NIL_UUID);
                        }
                    }
                });
            }
            LOGGER.info("Defeat. Challengers: " + challengersToString());
            getChallengeMobs().forEach(EntityUtils::killCompletely);
            setDirty();
        }
    }

    protected void dropRewards(ServerPlayerEntity player) {
        ChallengeMedalType type = getMedalTypeForFinishing(player);
        Objects.requireNonNull(type, "getMedalTypeForFinishing must not return non-null at first but then return null");
        ItemStack stack = type.createMedal();
        GameProfile profile = player.getGameProfile();
        stack.getOrCreateTag().put("Owner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Override
    public boolean isOver() {
        return isVictory() || isLoss();
    }

    public boolean isVictory() {
        return status == Status.VICTORY;
    }

    public boolean isLoss() {
        return status == Status.LOSS;
    }

    protected abstract void onVictory();

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean isStopped() {
        return status == Status.STOPPED;
    }

    @Override
    public void stop() {
        active = false;
        challengeInfo.getTimeInfo().removeAllPlayers();
        challengeInfo.getProgressInfo().removeAllPlayers();
        status = Status.STOPPED;
        removeAllChallengers();
        LOGGER.info("Stopped");
    }

    protected void initMedal(PlayerEntity player, ItemStack stack) {
        GameProfile profile = player.getGameProfile();
        stack.getOrCreateTag().put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), profile));
    }

    public List<MobEntity> getChallengeMobs() {
        return level.getEntities()
                .filter(entity -> entity instanceof MobEntity)
                .map(entity -> (MobEntity) entity)
                .filter(mob -> mob.getCapability(NonAPICapabilities.CHALLENGE_MOB)
                        .filter(challengeMob -> Objects.equals(challengeMob.getChallenge(), this))
                        .isPresent())
                .collect(Collectors.toList());
    }

    protected abstract int getTimeLimit();

    protected abstract float getProgress();

    protected boolean finishedChallenge(ServerPlayerEntity player) {
        return getMedalTypeForFinishing(player) != null;
    }

    @Nullable
    protected abstract ChallengeMedalType getMedalTypeForFinishing(ServerPlayerEntity player);

    @Nullable
    protected ChallengeInfo getCustomChallengeInfo() {
        return null;
    }

    public boolean hasChallenger(PlayerEntity player) {
        return challengerUUIDs.contains(player.getUUID());
    }

    public ChallengeType<?> getType() {
        return type;
    }

    public boolean isStrict() {
        return strict;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public boolean isCompletedInHardcore() {
        return completedInHardcore;
    }

    public void onChallengeMobDeath(DamageSource source, Entity deadEntity) {}

    @SuppressWarnings("deprecation")
    @Override
    protected void update() {
        active = level.hasChunkAt(center) && !getChallengers().isEmpty();
    }

    protected void setDirty() {
        ChallengeManager.byType(type, level).setDirty();
    }

    protected void groundZero() {
        level.getEntities()
                .filter(entity -> entity instanceof MobEntity)
                .filter(entity -> entity.distanceToSqr(Vector3d.atCenterOf(center)) <= getType().getFindPlayerDistance() * getType().getFindPlayerDistance())
                .forEach(EntityUtils::killCompletely);
//      Someone may want to use this mechanism to insta-kill some monsters to get their loots. That's impossible.
        level.getEntities().filter(entity -> entity instanceof ItemEntity).forEach(EntityUtils::killCompletely);
    }

    protected abstract Iterable<BaseSpawner<?>> getSpawners();

    protected abstract Iterable<ChallengeEvent> getEvents();

    protected abstract class BaseSpawner<T extends MobEntity> {
        private final Difficulty[] availableDifficulties = new Difficulty[]{Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD};
        protected final EntityType<T> spawnType;
        protected final int minSpawnCount;
        protected final int maxSpawnCount;
        protected final int minSpawnInterval;
        protected final int maxSpawnInterval;
        protected final double minSpawnDistance;
        protected final double spawnDistanceScale;
        protected int spawnTicksRemaining;

        protected BaseSpawner(EntityType<T> spawnType, int minSpawnCount, int maxSpawnCount, int minSpawnInterval, int maxSpawnInterval, double minSpawnDistance, double spawnDistanceScale) {
            checkArgs(spawnType, minSpawnCount, maxSpawnCount, minSpawnInterval, maxSpawnInterval, minSpawnDistance, spawnDistanceScale);
            this.spawnType = spawnType;
            this.minSpawnCount = minSpawnCount;
            this.maxSpawnCount = maxSpawnCount;
            this.minSpawnInterval = minSpawnInterval;
            this.maxSpawnInterval = maxSpawnInterval;
            this.minSpawnDistance = minSpawnDistance;
            this.spawnDistanceScale = spawnDistanceScale;
            spawnTicksRemaining = getRandomSpawnInterval() / 2;
        }

        private void checkArgs(EntityType<T> spawnType, int minSpawnCount, int maxSpawnCount, int minSpawnInterval, int maxSpawnInterval, double minSpawnDistance, double spawnDistanceScale) {
            Objects.requireNonNull(spawnType, "SpawnType should be non-null");
            Preconditions.checkArgument(minSpawnCount >= 0, "MinSpawnCount should not be negative");
            Preconditions.checkArgument(maxSpawnCount >= 0, "MaxSpawnCount should not be negative");
            Preconditions.checkArgument(maxSpawnCount >= minSpawnCount, "MaxSpawnCount should not be smaller than minSpawnCount");
            Preconditions.checkArgument(minSpawnInterval > 0, "MinSpawnInterval should be positive");
            Preconditions.checkArgument(maxSpawnInterval > 0, "MaxSpawnInterval should be positive");
            Preconditions.checkArgument(maxSpawnInterval >= minSpawnInterval, "MaxSpawnInterval should not be smaller than minSpawnInterval");
            Preconditions.checkArgument(minSpawnDistance >= 0, "MinSpawnDistance should not be negative");
            Preconditions.checkArgument(spawnDistanceScale >= 0, "SpawnDistanceScale should not be negative");
        }

        public int getRandomSpawnCount() {
            return minSpawnCount + random.nextInt(maxSpawnCount - minSpawnCount + 1);
        }

        public int getRandomSpawnInterval() {
            return minSpawnInterval + random.nextInt(maxSpawnInterval - minSpawnInterval + 1);
        }

        public double getRandomSpawnDistance() {
            double randomDistance = random.nextDouble() - random.nextDouble();
            return randomDistance * spawnDistanceScale + randomDistance >= 0 ? minSpawnDistance : -minSpawnDistance;
        }

        public Vector3d getRandomSpawnPosition() {
            Vector3d spawnPos = Vector3d.atBottomCenterOf(center);
            double spawnDistance = getRandomSpawnDistance();
            float randomAngle = (float) (random.nextDouble() * 2 * Math.PI);
            Vector3d offsetter = new Vector3d(MathHelper.cos(randomAngle) * spawnDistance, 0, MathHelper.sin(randomAngle) * spawnDistance);
            spawnPos = spawnPos.add(offsetter);
            return new Vector3d(spawnPos.x, level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(spawnPos)).getY(), spawnPos.z);
        }

        public void tick() {
            if (spawnTicksRemaining > 0) {
                spawnTicksRemaining--;
            } else {
                spawn();
                spawnTicksRemaining = getRandomSpawnInterval();
            }
        }

        protected void spawn() {
            if (!shouldSpawn()) {
                return;
            }
            int randomCount = getRandomSpawnCount();
            for (int i = 0; i < randomCount; i++) {
                T mob = spawnType.create(level);
                if (mob != null) {
                    mob.moveTo(getRandomSpawnPosition());
                    mob.yRot = random.nextFloat() * 360;
                    mob.getCapability(NonAPICapabilities.CHALLENGE_MOB).ifPresent(cap -> cap.setChallenge(Challenge.this));
                    mob.finalizeSpawn(level, level.getCurrentDifficultyAt(mob.blockPosition()), SpawnReason.EVENT, null, null);
                    mob.restrictTo(center, (int) (type.getFindPlayerDistance() + WARN_DISTANCE_OFFSET));
                    level.addFreshEntityWithPassengers(mob);
                    onMobSpawn(mob);
                    setDirty();
                }
            }
        }

        protected boolean shouldSpawn() {
            if (Arrays.asList(getAvailableDifficulties()).contains(difficulty)) {
                return getChallengeMobs().size() < getMobCountLimit();
            }
            return false;
        }

        protected Difficulty[] getAvailableDifficulties() {
            return availableDifficulties;
        }

        protected int getMobCountLimit() {
            return 15;
        }

        protected abstract void onMobSpawn(T mob);
    }

    protected class FireballDropEvent extends ChallengeEvent {
        protected final double fireballFrequency;
        protected final EntityType<? extends ProjectileEntity> fireballType;

        public FireballDropEvent(String id, int minInterval, int maxInterval, int maxDelayTicks, double fireballFrequency, EntityType<? extends ProjectileEntity> fireballType) {
            super(id, minInterval, maxInterval, maxDelayTicks, 120);
            this.fireballFrequency = fireballFrequency;
            this.fireballType = fireballType;
        }

        @Override
        protected void preApply(ServerPlayerEntity challenger) {
            challenger.sendMessage(new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "warning." + Utils.getRegistryName(fireballType).getPath())).withStyle(TextFormatting.RED), Util.NIL_UUID);
        }

        @Override
        protected void startApplyingOn(ServerPlayerEntity challenger) {}

        @Override
        protected void applyEventTicks(ServerPlayerEntity challenger) {
            for (int i = 0; i < getFireballCount(); i++) {
                ProjectileEntity projectile = fireballType.create(level);
                if (projectile != null) {
                    handleProjectilePositionAndMovement(challenger, projectile);
                    projectile.getCapability(NonAPICapabilities.CHALLENGE_MOB).ifPresent(cap -> cap.setChallenge(Challenge.this));
                    level.addFreshEntity(projectile);
                }
            }
        }

        private void handleProjectilePositionAndMovement(ServerPlayerEntity challenger, ProjectileEntity projectile) {
            Vector3d position = getFireballPosition(challenger);
            projectile.moveTo(position.x, position.y, position.z, projectile.yRot, projectile.xRot);
            projectile.setPos(projectile.position().x, projectile.position().y, projectile.position().z);
            if (projectile instanceof DamagingProjectileEntity) {
                ((DamagingProjectileEntity) projectile).xPower = 0;
                ((DamagingProjectileEntity) projectile).yPower = -0.1;
                ((DamagingProjectileEntity) projectile).zPower = 0;
            }
        }

        private int getFireballCount() {
            double tickFrequency = fireballFrequency / 20;
            int minCount = MathHelper.floor(tickFrequency);
            int extraCount = random.nextDouble() < (tickFrequency - minCount) ? 1 : 0;
            return minCount + extraCount;
        }

        private Vector3d getFireballPosition(ServerPlayerEntity challenger) {
            double deviation = getDeviation(challenger);
            Vector3d position = challenger.position();
            double xOffs = (random.nextDouble() - random.nextDouble()) * deviation;
            double yOffs = getHeight(challenger) + random.nextDouble() * 2;
            double zOffs = (random.nextDouble() - random.nextDouble()) * deviation;
            return position.add(xOffs, yOffs, zOffs);
        }

        protected double getDeviation(ServerPlayerEntity challenger) {
            return 10;
        }

        protected double getHeight(ServerPlayerEntity challenger) {
            return 10;
        }

        @Override
        protected void postApply(ServerPlayerEntity challenger) {}
    }

    protected class ExtraMobSpawnEvent extends ChallengeEvent {
        protected final boolean shouldMobJoinChallenge;
        protected final Supplier<? extends EffectInstance> glowingEffectSupplier;
        protected final Pair<? extends EntityType<? extends MobEntity>, Integer>[] possibleMobs;

        @SafeVarargs
        public ExtraMobSpawnEvent(String id, int minInterval, int maxInterval, int maxDelayTicks, boolean shouldMobJoinChallenge, Supplier<? extends EffectInstance> glowingEffectSupplier, Pair<EntityType<? extends MobEntity>, Integer>... possibleMobs) {
            super(id, minInterval, maxInterval, maxDelayTicks);
            this.shouldMobJoinChallenge = shouldMobJoinChallenge;
            this.glowingEffectSupplier = glowingEffectSupplier;
            this.possibleMobs = possibleMobs;
        }

        @Override
        protected void preApply(ServerPlayerEntity challenger) {
            challenger.sendMessage(new TranslationTextComponent(SoulCraft.prefixMsg("challenge","warning.extra_monsters")).withStyle(TextFormatting.RED), Util.NIL_UUID);
        }

        @Override
        protected void startApplyingOn(ServerPlayerEntity challenger) {
            for (Pair<? extends EntityType<? extends MobEntity>, Integer> pair : possibleMobs) {
                EntityType<? extends MobEntity> mobType = pair.getFirst();
                int mobCount = pair.getSecond();
                for (int i = 0; i < mobCount; i++) {
                    MobEntity mob = mobType.create(level);
                    if (mob != null) {
                        Vector3d position = getRandomPosition(challenger);
                        mob.moveTo(position.x, position.y, position.z, -challenger.yRot, 0);
                        mob.getLookControl().setLookAt(challenger.getEyePosition(1));
                        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(new BlockPos(position)), SpawnReason.EVENT, null, null);
                        if (shouldMobJoinChallenge) {
                            mob.getCapability(NonAPICapabilities.CHALLENGE_MOB).ifPresent(cap -> cap.setChallenge(Challenge.this));
                        }
                        applyMob(mob, challenger);
                        level.addFreshEntityWithPassengers(mob);
                    }
                }
            }
        }

        protected Vector3d getRandomPosition(ServerPlayerEntity challenger) {
            Vector3d position = Vectors.midpoint(challenger.position(), Vector3d.atCenterOf(center));
            double xOffs = -getHorizontalOffset(challenger) + random.nextDouble() * getHorizontalOffset(challenger) * 2;
            double zOffs = -getHorizontalOffset(challenger) + random.nextDouble() * getHorizontalOffset(challenger) * 2;
            double y = level.getHeightmapPos(Heightmap.Type.WORLD_SURFACE, new BlockPos(position)).getY() + getVerticalOffset(challenger);
            return new Vector3d(position.x + xOffs, y, position.z + zOffs);
        }

        protected double getHorizontalOffset(ServerPlayerEntity challenger) {
            return 4;
        }

        protected double getVerticalOffset(ServerPlayerEntity challenger) {
            return 15 + random.nextDouble() * 5;
        }

        protected void applyMob(MobEntity mob, ServerPlayerEntity challenger) {
            mob.addEffect(new EffectInstance(Effects.SLOW_FALLING, 20 * 10));
            mob.addEffect(glowingEffectSupplier.get());
            if (EntityPredicates.ATTACK_ALLOWED.test(challenger)) {
                EntityUtils.setTarget(mob, challenger);
            }
        }

        @Override
        protected void applyEventTicks(ServerPlayerEntity challenger) {}

        @Override
        protected void postApply(ServerPlayerEntity challenger) {}
    }

    protected abstract class ChallengeEvent {
        protected final String id;
        protected final int minInterval;
        protected final int maxInterval;
        protected final int maxDelayTicks;
        protected final int duration;
        protected int ticksRemaining;

        protected ChallengeEvent(String id, int minInterval, int maxInterval, int maxDelayTicks) {
            this(id, minInterval, maxInterval, maxDelayTicks, 0);
        }

        protected ChallengeEvent(String id, int minInterval, int maxInterval, int maxDelayTicks, int duration) {
            checkArgs(minInterval, maxInterval, maxDelayTicks, duration);
            this.id = id;
            this.minInterval = minInterval;
            this.maxInterval = maxInterval;
            this.maxDelayTicks = maxDelayTicks;
            this.duration = duration;
            ticksRemaining = getRandomEventInterval();
        }

        private void checkArgs(int minInterval, int maxInterval, int maxDelayTicks, int duration) {
            Preconditions.checkArgument(minInterval > 0, "MinInterval should be positive");
            Preconditions.checkArgument(maxInterval > 0, "MaxInterval should be positive");
            Preconditions.checkArgument(maxInterval >= minInterval, "MaxInterval should not be smaller than minInterval");
            Preconditions.checkArgument(maxDelayTicks > 0, "MaxDelayTicks should be positive");
            Preconditions.checkArgument(duration >= 0, "Duration should not be negative");
        }

        public void tick() {
            if (ticksRemaining > 0) {
                ticksRemaining--;
                if (ticksRemaining == maxDelayTicks) {
                    getChallengers().forEach(this::preApply);
                }
            } else if (ticksRemaining < 0) {
                getChallengers().forEach(this::applyEventTicks);
                ticksRemaining++;
                if (ticksRemaining == 0) {
                    getChallengers().forEach(this::postApply);
                    ticksRemaining = getRandomEventInterval();
                }
            } else {
                getChallengers().forEach(this::startApplyingOn);
                if (isInstant()) {
                    ticksRemaining = getRandomEventInterval();
                } else {
                    ticksRemaining = -duration;
                }
            }
        }

        public boolean isInstant() {
            return duration == 0;
        }

        public int getRandomEventInterval() {
            return minInterval + random.nextInt(maxInterval - minInterval + 1);
        }

        protected abstract void preApply(ServerPlayerEntity challenger);

        protected abstract void startApplyingOn(ServerPlayerEntity challenger);

        protected abstract void applyEventTicks(ServerPlayerEntity challenger);

        protected abstract void postApply(ServerPlayerEntity challenger);

        public CompoundNBT save() {
            CompoundNBT compoundNBT = new CompoundNBT();
            compoundNBT.putString("ChallengeEventId", id);
            compoundNBT.putInt("TicksRemaining", ticksRemaining);
            return compoundNBT;
        }

        public CompoundNBT findNBTFrom(ListNBT listNBT) {
            List<CompoundNBT> possibleNBTs = new ArrayList<>();
            for (int i = 0; i < listNBT.size(); i++) {
                CompoundNBT compoundNBT = listNBT.getCompound(i);
                if (compoundNBT.contains("ChallengeEventId", Constants.NBT.TAG_STRING)) {
                    String id = compoundNBT.getString("ChallengeEventId");
                    if (Objects.equals(id, this.id)) {
                        possibleNBTs.add(compoundNBT);
                    }
                }
            }
            Preconditions.checkState(!possibleNBTs.isEmpty() && possibleNBTs.get(0) != null, "No valid NBT found in the ListNBT " + listNBT);
            Preconditions.checkState(possibleNBTs.size() == 1, "More than 1 NBT found in the ListNBT " + listNBT + ". Maybe there are duplicate ids.");
            return possibleNBTs.get(0);
        }

        public void load(CompoundNBT compoundNBT) {
            ticksRemaining = compoundNBT.getInt("TicksRemaining");
        }
    }

    protected static class ChallengeInfo {
        private final ServerBossInfo timeInfo;
        private final ServerBossInfo progressInfo;

        public ChallengeInfo(ServerBossInfo timeInfo, ServerBossInfo progressInfo) {
            this.timeInfo = timeInfo;
            this.progressInfo = progressInfo;
        }

        public ServerBossInfo getTimeInfo() {
            return timeInfo;
        }

        public ServerBossInfo getProgressInfo() {
            return progressInfo;
        }
    }

    public static class LoseReason {
        @Nullable
        private final ITextComponent detailText;

        public LoseReason() {
            this(null);
        }

        public LoseReason(@Nullable ITextComponent detailText) {
            this.detailText = detailText;
        }

        public boolean hasDetailText() {
            return detailText != null;
        }

        public ITextComponent getDetailText() {
            if (!hasDetailText()) {
                throw new UnsupportedOperationException("No detailText found");
            }
            return new TranslationTextComponent(SoulCraft.prefixMsg("challenge", "lose_reason"), detailText).withStyle(TextFormatting.RED);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("detailText", detailText).toString();
        }
    }

    public enum Status {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        public static Status byName(String name) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(name)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid name: " + name);
        }
    }
}
