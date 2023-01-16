package lych.soulcraft.listener;

import com.google.common.collect.Streams;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.api.shield.ISharedShield;
import lych.soulcraft.api.shield.ISharedShieldUser;
import lych.soulcraft.api.shield.IShieldUser;
import lych.soulcraft.capability.ChallengeMobProvider;
import lych.soulcraft.capability.IChallengeMob;
import lych.soulcraft.capability.NonAPICapabilities;
import lych.soulcraft.effect.ModEffects;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.entity.ai.goal.wrapper.Goals;
import lych.soulcraft.entity.iface.*;
import lych.soulcraft.entity.monster.boss.SkeletonKingEntity;
import lych.soulcraft.entity.monster.boss.esv.SoulCrystalEntity;
import lych.soulcraft.entity.projectile.SoulArrowEntity;
import lych.soulcraft.extension.ExtraAbility;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.soulpower.buff.PlayerBuffMap;
import lych.soulcraft.extension.soulpower.control.SoulManager;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcements;
import lych.soulcraft.extension.soulpower.reinforce.WandererReinforcement;
import lych.soulcraft.extension.superlink.SuperLinkManager;
import lych.soulcraft.item.ModItems;
import lych.soulcraft.item.SoulPieceItem;
import lych.soulcraft.mixin.EntityDamageSourceAccessor;
import lych.soulcraft.mixin.IndirectEntityDamageSourceAccessor;
import lych.soulcraft.mixin.MobSpawnInfoAccessor;
import lych.soulcraft.util.CollectionUtils;
import lych.soulcraft.util.EntityUtils;
import lych.soulcraft.util.mixin.IEntityMixin;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import lych.soulcraft.world.CommandData;
import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.challenge.SurvivalChallenge;
import lych.soulcraft.world.event.manager.ChallengeManager;
import lych.soulcraft.world.event.manager.EventManager;
import lych.soulcraft.world.event.manager.WorldTickerManager;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static lych.soulcraft.util.ModConstants.Exa.FALL_BUFFER_AMOUNT;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID)
public final class CommonEventListener {
    private CommonEventListener() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().level.isClientSide()) {
            EventManager.runEvents();
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (event.getSource().getEntity() instanceof SkeletonKingEntity && event.getEntity() instanceof AbstractSkeletonEntity) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void onLivingStartHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof IHasOwner<?>) {
            if (event.getSource() instanceof IndirectEntityDamageSource) {
                IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getEntity();
                if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetIndirectDamageToOwner()) {
                    ((IndirectEntityDamageSourceAccessor) event.getSource()).setOwner(entityWithOwner.getOwner());
                }
            }
            if (event.getSource() instanceof EntityDamageSource) {
                IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getEntity();
                if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetDirectDamageToOwner()) {
                    ((EntityDamageSourceAccessor) event.getSource()).setEntity(entityWithOwner.getOwner());
                }
            }
        }
        if (event.getSource() instanceof IndirectEntityDamageSource && event.getSource().getEntity() != event.getSource().getDirectEntity() && event.getSource().getDirectEntity() instanceof IHasOwner<?>) {
            IHasOwner<?> entityWithOwner = (IHasOwner<?>) event.getSource().getDirectEntity();
            if (entityWithOwner.getOwner() != null && entityWithOwner.shouldSetDirectDamageToOwner()) {
                ((EntityDamageSourceAccessor) event.getSource()).setEntity(entityWithOwner.getOwner());
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
//        if (!OptiFineHandler.isOptiFineLoaded()) {
        if (event.getSource() == DamageSource.ON_FIRE && ((IEntityMixin) event.getEntity()).isOnSoulFire()) {
            event.setAmount(event.getAmount() * 2);
        }
        if (event.getSource().getDirectEntity() instanceof SoulArrowEntity && ((IEntityMixin) event.getSource().getDirectEntity()).isOnSoulFire()) {
            ((IEntityMixin) event.getEntity()).setOnSoulFire(true);
        }
//        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFinallyHurt(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof IDamageMultipliable && !((IDamageMultipliable) event.getSource().getEntity()).multiplyFinalDamage()) {
            event.setAmount(event.getAmount() * ((IDamageMultipliable) event.getSource().getEntity()).getDamageMultiplier());
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) {
        if (!event.getSource().isBypassInvul() && event.getEntity() instanceof SkeletonKingEntity && ((SkeletonKingEntity) event.getEntity()).reachedTier(11)) {
            event.setAmount(Math.min(event.getAmount(), Math.max(SkeletonKingEntity.DAMAGE_THRESHOLD_T11 - ((SkeletonKingEntity) event.getEntity()).getTier() * 2 + 22, SkeletonKingEntity.MIN_MAX_DAMAGE)));
        }
        if (event.getSource().getEntity() instanceof IDamageMultipliable && ((IDamageMultipliable) event.getSource().getEntity()).multiplyFinalDamage()) {
            event.setAmount(event.getAmount() * ((IDamageMultipliable) event.getSource()).getDamageMultiplier());
        }
        if (!event.getSource().isBypassInvul() && event.getEntity() instanceof IHasResistance) {
            event.setAmount(event.getAmount() * (1 - ((IHasResistance) event.getEntity()).getResistance()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onLivingFinallyDamage(LivingDamageEvent event) {
        if (event.getEntity() instanceof ITieredMob) {
            ITieredMob entity = (ITieredMob) event.getEntity();
            entity.handleHurt(event.getSource());
        }
        if (event.getEntity() instanceof IShieldUser) {
            @Nullable
            ISharedShield shield = ((IShieldUser) event.getEntity()).getSharedShield();
            if (shield != null && ((IShieldUser) event.getEntity()).isShieldValid()) {
                if (event.getAmount() > 0 && event.getEntity().level instanceof ServerWorld && ((IShieldUser) event.getEntity()).showHitParticles(event.getSource(), event.getAmount())) {
                    EntityUtils.sharedShieldHitParticleServerside(event.getEntity(), (ServerWorld) event.getEntity().level);
                }
                float amount = shield.hurt(event.getSource(), event.getAmount());
                if (amount > 0) {
                    event.setAmount(amount);
                } else {
                    event.setCanceled(true);
                }
                if (shield.getHealth() <= 0) {
                    if (event.getEntity() instanceof ISharedShieldUser) {
                        ISharedShieldUser user = (ISharedShieldUser) event.getEntity();
                        if (user.getShieldProvider() instanceof Entity) {
                            user.getShieldProvider().onShieldExhausted();
                            if (user.hasConsumableShield()) {
                                user.getShieldProvider().setSharedShield(null);
                                user.getShieldProvider().onShieldBreak();
                                if (event.getEntity().level instanceof ServerWorld) {
                                    EntityUtils.addParticlesAroundSelfServerside((Entity) user.getShieldProvider(), (ServerWorld) event.getEntity().level, ParticleTypes.EXPLOSION, 5 + event.getEntityLiving().getRandom().nextInt(3));
                                }
                            }
                        }
                    } else {
                        IShieldUser user = (IShieldUser) event.getEntity();
                        user.onShieldExhausted();
                        if (user.hasConsumableShield()) {
                            user.setSharedShield(null);
                            user.onShieldBreak();
                            if (event.getEntity().level instanceof ServerWorld) {
                                EntityUtils.addParticlesAroundSelfServerside(event.getEntity(), (ServerWorld) event.getEntity().level, ParticleTypes.EXPLOSION, 5 + event.getEntityLiving().getRandom().nextInt(3));
                            }
                        }
                    }
                }
            }
        }
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            for (SurvivalChallenge challenge : ChallengeManager.byType(ChallengeType.SURVIVAL, player.getLevel())) {
                if (challenge.hasChallenger(player)) {
                    if (event.getSource().getEntity() != null && event.getSource().getEntity().getCapability(NonAPICapabilities.CHALLENGE_MOB).map(cap -> Objects.equals(cap.getChallenge(), challenge)).orElse(false)) {
                        challenge.addDamageFor(player.getUUID(), event.getAmount());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.PURIFICATION.isOn((PlayerEntity) event.getEntity())) {
            boolean harmful = EntityUtils.isHarmful(event.getPotionEffect());
            if (harmful) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            for (PlayerBuff buff : PlayerBuffMap.values().stream().filter(buff -> PlayerBuffMap.getAbility(buff).orElseThrow(NullPointerException::new).isOn(event.player)).collect(Collectors.toList())) {
                buff.tick(event.player, (ServerWorld) event.player.level);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().level.isClientSide) {
            ((IEntityMixin) event.getEntityLiving()).setReversed(event.getEntityLiving().hasEffect(ModEffects.REVERSION));
        }
        if (event.getEntityLiving() instanceof IShieldUser && ((IShieldUser) event.getEntityLiving()).getSharedShield() != null) {
            ((IShieldUser) event.getEntityLiving()).getSharedShield().tick();
        }
        if (event.getEntityLiving() instanceof ISpellCastable) {
            ((ISpellCastable) event.getEntityLiving()).renderParticles();
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ITieredMob) {
            ITieredMob entity = (ITieredMob) event.getEntity();
            entity.handleDeath(event.getSource());
        }
        if (!event.getEntity().level.isClientSide()) {
            ServerWorld world = (ServerWorld) event.getEntity().level;
            world.getEntities().filter(entity -> entity instanceof INecromancer<?, ?>).forEach(entity -> {
                INecromancer<?, ?> necromancer = (INecromancer<?, ?>) entity;
                if (entity.distanceToSqr(event.getEntity()) <= necromancer.getReviveDistanceSqr()) {
                    necromancer.addSoulIfPossible(event.getEntity(), world);
                }
            });
            event.getEntity().getCapability(NonAPICapabilities.CHALLENGE_MOB).filter(cap -> cap.getChallenge() != null).map(IChallengeMob::getChallenge).ifPresent(challenge -> challenge.onChallengeMobDeath(event.getSource(), event.getEntity()));
        }
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        double dropProbability = Reinforcements.getDropProbability(event.getEntityLiving().getType());
        boolean playerKilled = event.isRecentlyHit() && event.getSource().getEntity() instanceof PlayerEntity;
        boolean canLoot = event.getEntityLiving().level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT);
        boolean nonDeadPlayer = !(event.getEntityLiving() instanceof PlayerEntity);
        if (playerKilled && canLoot && nonDeadPlayer) {
            PlayerEntity player = (PlayerEntity) event.getSource().getEntity();
            int level = Reinforcements.WANDERER.getLevel(player.getMainHandItem());
            dropProbability *= (1 + level * WandererReinforcement.PROBABILITY_MULTIPLIER);
            while (event.getEntityLiving().getRandom().nextDouble() < dropProbability) {
                ItemStack piece = new ItemStack(ModItems.SOUL_PIECE);
                SoulPieceItem.setType(piece, event.getEntityLiving().getType());
                event.getEntityLiving().spawnAtLocation(piece);
                dropProbability -= 1;
                if (dropProbability <= 0) {
                    break;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoadWithHighPriority(BiomeLoadingEvent event) {
        if (event.getName() != null && "soul_sand_valley".equals(event.getName().getPath())) {
            event.getSpawns().addMobCharge(ModEntities.SOUL_SKELETON, 0.7, 0.15);
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 20, 5, 5));
        }
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (event.getName() != null && "soul_sand_valley".equals(event.getName().getPath())) {
            ((MobSpawnInfoAccessor) event.getSpawns()).getSpawners().get(EntityClassification.MONSTER).removeIf(spawners -> spawners.type == EntityType.SKELETON);
            ((MobSpawnInfoAccessor) event.getSpawns()).getMobSpawnCosts().remove(EntityType.SKELETON);
        }
    }

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject().level instanceof ServerWorld) {
            event.addCapability(SoulCraft.prefix("challenge_mob"), new ChallengeMobProvider((ServerWorld) event.getObject().level));
        }
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isClientSide() && event.getEntity() instanceof MobEntity) {
            ServerWorld level = (ServerWorld) event.getWorld();
            MobEntity mob = (MobEntity) event.getEntity();
            if (mob instanceof CreatureEntity) {
                mob.goalSelector.addGoal(100, Goals.of(new MoveTowardsRestrictionGoal((CreatureEntity) mob, 1)).executeIf(() -> event.getEntity().getCapability(NonAPICapabilities.CHALLENGE_MOB).map(cap -> cap.getChallenge() != null).orElse(false)).get());
            }
            if (mob instanceof SoulCrystalEntity) {
                if (!((SoulCrystalEntity) mob).isValid()) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onPlayerChangeGameMode(PlayerEvent.PlayerChangeGameModeEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            if (event.getNewGameMode() == GameType.CREATIVE || event.getNewGameMode() == GameType.SPECTATOR) {
                makeChallengesDefeated(player, true, Challenge.GAME_MODE);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onChallengerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            makeChallengesDefeated(player, false, Challenge.EMPTY);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity oldPlayer = event.getOriginal();
        PlayerEntity newPlayer = event.getPlayer();
        IPlayerEntityMixin oldPlayerM = (IPlayerEntityMixin) oldPlayer;
        IPlayerEntityMixin newPlayerM = (IPlayerEntityMixin) newPlayer;
        syncData(oldPlayerM, newPlayerM);
    }

    private static void syncData(IPlayerEntityMixin oldPlayerM, IPlayerEntityMixin newPlayerM) {
        Set<IExtraAbility> extraAbilities = oldPlayerM.getExtraAbilities();
        newPlayerM.setExtraAbilities(extraAbilities);
        Map<EntityType<?>, Integer> bossTierMap = oldPlayerM.getBossTierMap();
        CollectionUtils.refill(newPlayerM.getBossTierMap(), bossTierMap);
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        ServerWorld world = (ServerWorld) event.world;
        if (event.phase == TickEvent.Phase.END) {
            tick(world);
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (ExtraAbility.FALLING_BUFFER.isOn(player)) {
                event.setDistance(Math.max(event.getDistance() - FALL_BUFFER_AMOUNT, 0));
            }
        }
    }

    private static void tick(ServerWorld world) {
        ChallengeManager.all(world).forEach(EventManager::tick);
        CommandData.get(world.getServer()).update(world);
        EntityHighlightManager.get(world).tick();
        SoulManager.get(world).tick();
        SuperLinkManager.get(world).tick(world.getServer());
        WorldTickerManager.get(world).tick();
    }

    private static void makeChallengesDefeated(ServerPlayerEntity player, boolean onlyStrictChallenges, Challenge.LoseReason reason) {
        ChallengeManager.all(player.getLevel()).stream()
                .flatMap(Streams::stream)
                .filter(challenge -> challenge.hasChallenger(player))
                .filter(challenge -> !onlyStrictChallenges || challenge.isStrict())
                .forEach(challenge -> challenge.lose(reason, reason, null));
    }
}
