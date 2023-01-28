package lych.soulcraft.listener;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.event.PostLivingHurtEvent;
import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.api.shield.ISharedShield;
import lych.soulcraft.api.shield.ISharedShieldUser;
import lych.soulcraft.api.shield.IShieldUser;
import lych.soulcraft.block.IArmoredBlock;
import lych.soulcraft.block.ModBlocks;
import lych.soulcraft.block.plant.SoulifiedBushBlock;
import lych.soulcraft.capability.ChallengeMobProvider;
import lych.soulcraft.capability.IChallengeMob;
import lych.soulcraft.capability.NonAPICapabilities;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.effect.ModEffects;
import lych.soulcraft.effect.SoulPollutionHandler;
import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.entity.ai.goal.wrapper.Goals;
import lych.soulcraft.entity.iface.*;
import lych.soulcraft.entity.monster.boss.SkeletonKingEntity;
import lych.soulcraft.entity.monster.boss.esv.SoulCrystalEntity;
import lych.soulcraft.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soulcraft.entity.projectile.SoulArrowEntity;
import lych.soulcraft.extension.ExtraAbility;
import lych.soulcraft.extension.highlight.EntityHighlightManager;
import lych.soulcraft.extension.skull.ModSkulls;
import lych.soulcraft.extension.soulpower.buff.PlayerBuffMap;
import lych.soulcraft.extension.soulpower.control.SoulManager;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcements;
import lych.soulcraft.extension.soulpower.reinforce.WandererReinforcement;
import lych.soulcraft.extension.superlink.SuperLinkManager;
import lych.soulcraft.item.ModItems;
import lych.soulcraft.item.SoulPieceItem;
import lych.soulcraft.item.VoidwalkerSpawnEggItem;
import lych.soulcraft.mixin.EntityDamageSourceAccessor;
import lych.soulcraft.mixin.IndirectEntityDamageSourceAccessor;
import lych.soulcraft.mixin.MobSpawnInfoAccessor;
import lych.soulcraft.network.ClickHandlerNetwork;
import lych.soulcraft.tag.ModBlockTags;
import lych.soulcraft.util.*;
import lych.soulcraft.util.mixin.IEntityMixin;
import lych.soulcraft.util.mixin.IPlayerEntityMixin;
import lych.soulcraft.world.CommandData;
import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import lych.soulcraft.world.event.challenge.SurvivalChallenge;
import lych.soulcraft.world.event.manager.ChallengeManager;
import lych.soulcraft.world.event.manager.EventManager;
import lych.soulcraft.world.event.manager.WorldTickerManager;
import lych.soulcraft.world.gen.feature.ModConfiguredFeatures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherrackBlock;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import static lych.soulcraft.util.ExtraAbilityConstants.FALL_BUFFER_AMOUNT;

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
        if (event.getEntity() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.getEntity();
            if (((IPlayerEntityMixin) player).hasExtraAbility(ExtraAbility.DRAGON_WIZARD) && event.getSource().isMagic()) {
                event.setCanceled(true);
            }
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
        if (event.getSource() == DamageSource.ON_FIRE) {
            event.setAmount(event.getAmount() * Math.max(1, ((IEntityMixin) event.getEntity()).getFireOnSelf().getFireDamage(event.getEntity(), event.getEntity().level)));
        }
        if (event.getSource().getDirectEntity() instanceof SoulArrowEntity) {
            ((IEntityMixin) event.getEntity()).setFireOnSelf(((IEntityMixin) event.getSource().getDirectEntity()).getFireOnSelf());
        }
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.THORNS_MASTER.isOn((PlayerEntity) event.getEntity())) {
            if (EntityUtils.isMelee(event.getSource()) && !EntityUtils.isThorns(event.getSource()) && event.getSource().getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
                attacker.hurt(DamageSource.thorns(event.getEntity()), ExtraAbilityConstants.THORNS_MASTER_DAMAGE);
            }
        }
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
    public static void onPostHurt(PostLivingHurtEvent event) {
        if (event.isSuccessfullyHurt()) {
            Entity attacker = event.getSource().getEntity();
            Entity target = event.getEntity();
            if (attacker instanceof PlayerEntity) {
                SoulPollutionHandler.mayPollute((PlayerEntity) attacker, attacker.level, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
            }
            if (target instanceof PlayerEntity) {
                SoulPollutionHandler.mayPollute((PlayerEntity) target, target.level, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
            }
        }
    }

    @SubscribeEvent
    public static void onSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (event.getEntity() instanceof AbstractVoidwalkerEntity && event.getSpawnReason() == SpawnReason.SPAWN_EGG) {
            ((AbstractVoidwalkerEntity) event.getEntity()).setTier(VoidwalkerSpawnEggItem.getCurrentTier());
        }
    }

    @SubscribeEvent
    public static void onBonemealApply(BonemealEvent event) {
        if (event.getBlock().is(Blocks.SOUL_SOIL) && canSoulSoilApplyBonemeal(event.getWorld(), event.getPos(), true)) {
            performHyphalify(event.getWorld(), event.getWorld().getRandom(), event.getPos(), ModBlocks.CRIMSON_HYPHAL_SOIL.defaultBlockState(), ModBlocks.WARPED_HYPHAL_SOIL.defaultBlockState());
            event.setResult(Event.Result.ALLOW);
        }
        if (event.getBlock().getBlock() instanceof NetherrackBlock) {
            if (canSoulSoilApplyBonemeal(event.getWorld(), event.getPos(), false)) {
                performHyphalify(event.getWorld(), event.getWorld().getRandom(), event.getPos(), Blocks.CRIMSON_NYLIUM.defaultBlockState(), Blocks.WARPED_NYLIUM.defaultBlockState());
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    private static boolean canSoulSoilApplyBonemeal(IBlockReader reader, BlockPos pos, boolean checkNyliums) {
        if (reader.getBlockState(pos.above()).propagatesSkylightDown(reader, pos)) {
            for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
                if ((checkNyliums && reader.getBlockState(nearbyPos).is(BlockTags.NYLIUM)) || reader.getBlockState(nearbyPos).is(ModBlockTags.HYPHAL_SOUL_SOIL)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void performHyphalify(World world, Random random, BlockPos pos, BlockState forCrimson, BlockState forWarped) {
        boolean crimson = false;
        boolean warped = false;
        for (BlockPos nearbyPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
            BlockState state = world.getBlockState(nearbyPos);
            if (state.is(Blocks.CRIMSON_NYLIUM) || state.is(ModBlocks.CRIMSON_HYPHAL_SOIL)) {
                crimson = true;
            }
            if (state.is(Blocks.WARPED_NYLIUM) || state.is(ModBlocks.WARPED_HYPHAL_SOIL)) {
                warped = true;
            }
        }
        if (crimson && warped) {
            world.setBlock(pos, random.nextBoolean() ? forCrimson : forWarped, Constants.BlockFlags.DEFAULT);
        } else if (crimson) {
            world.setBlock(pos, forCrimson, Constants.BlockFlags.DEFAULT);
        } else if (warped) {
            world.setBlock(pos, forWarped, Constants.BlockFlags.DEFAULT);
        } else if (ConfigHelper.shouldFailhard()) {
            throw new AssertionError("Neither nylium nor hyphal soul soil found");
        }
    }

    @SubscribeEvent
    public static void onBurnTimeGet(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().getItem() == ModItems.SOULIFIED_BUSH) {
            event.setBurnTime(SoulifiedBushBlock.BURN_TIME);
        }
    }

    @SubscribeEvent
    public static void onPlayerDig(PlayerEvent.BreakSpeed event) {
        if (ExtraAbility.DESTROYER.isOn(event.getPlayer())) {
            event.setNewSpeed(event.getNewSpeed() * ExtraAbilityConstants.DESTROYER_SPEED_MULTIPLIER);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityDestroyBlock(LivingDestroyBlockEvent event) {
        if (handleArmoredBlockBreak(event.getEntity().level, event.getPos(), IArmoredBlock::enableForMobs)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerDestroyBlock(BlockEvent.BreakEvent event) {
        World world = (World) event.getWorld();
        if (handleArmoredBlockBreak(world, event.getPos())) {
            event.setCanceled(true);
        }
        SoulPollutionHandler.mayPollute(event.getPlayer(), world, SoulPollutionHandler.POLLUTE_PROBABILITY_SPECIAL);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> handleArmoredBlockBreak(event.getWorld(), pos, IArmoredBlock::enableForExplosion));
    }

    private static boolean handleArmoredBlockBreak(World world, BlockPos pos) {
        return handleArmoredBlockBreak(world, pos, b -> true);
    }

    private static boolean handleArmoredBlockBreak(World world, BlockPos pos, Predicate<? super IArmoredBlock> shouldEnable) {
        BlockState old = world.getBlockState(pos);
        Block oldBlock = old.getBlock();
        TileEntity oldBlockEntity = world.getBlockEntity(pos);
        if (oldBlock instanceof IArmoredBlock) {
            IArmoredBlock armoredBlock = (IArmoredBlock) oldBlock;
            if (!shouldEnable.test(armoredBlock)) {
                return false;
            }
            BlockState child = armoredBlock.getChild(world, pos, old, oldBlockEntity);
            if (child != null) {
                world.setBlock(pos, child, Constants.BlockFlags.DEFAULT);
                armoredBlock.restoreFrom(world, pos, old, child, oldBlockEntity);
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onEmptyClick(PlayerInteractEvent.RightClickEmpty event) {
        ClickHandlerNetwork.INSTANCE.sendToServer(Unit.INSTANCE);
    }

    @SuppressWarnings("deprecation")
    public static void handleEmptyClickServerside(ServerPlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }
        if (player.getMainHandItem().isEmpty() && ExtraAbility.TELEPORTATION.isOn(player)) {
            int cooldown = ((IPlayerEntityMixin) player).getAdditionalCooldowns().getCooldownRemaining(ExtraAbility.TELEPORTATION.getRegistryName());
            if (cooldown == 0) {
                BlockRayTraceResult ray = (BlockRayTraceResult) player.pick(player.getAttributeValue(ForgeMod.REACH_DISTANCE.get()) + ExtraAbilityConstants.BASE_TELEPORTATION_RADIUS, 0, false);
                BlockPos pos = ray.getBlockPos();
                World world = player.getLevel();
                if (world.getBlockState(pos).getMaterial().blocksMotion() && world.getBlockState(pos.above()).isAir() && world.getBlockState(pos.above().above()).isAir()) {
                    player.teleportTo(ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
                    ((IPlayerEntityMixin) player).getAdditionalCooldowns().addCooldown(ExtraAbility.TELEPORTATION.getRegistryName(), ExtraAbilityConstants.TELEPORTATION_COOLDOWN);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEffectFirstlyApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.TRANSFORMATION.isOn((PlayerEntity) event.getEntity())) {
            if (event.getPotionEffect().getEffect() == Effects.WITHER) {
                event.getEntityLiving().addEffect(ModEffectUtils.copyAttributes(Effects.REGENERATION, event.getPotionEffect()));
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectApply(PotionEvent.PotionApplicableEvent event) {
        if (event.getEntity() instanceof PlayerEntity && ExtraAbility.PURIFICATION.isOn((PlayerEntity) event.getEntity())) {
            boolean harmful = ModEffectUtils.isHarmful(event.getPotionEffect());
            if (harmful && ((PlayerEntity) event.getEntity()).getRandom().nextDouble() < ExtraAbilityConstants.PURIFICATION_PROBABILITY) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<PlayerBuff> buffs = new ArrayList<>();
            for (PlayerBuff playerBuff : PlayerBuffMap.values()) {
                if (PlayerBuffMap.getAbility(playerBuff).orElseThrow(NullPointerException::new).isOn(event.player)) {
                    buffs.add(playerBuff);
                }
            }
            for (PlayerBuff buff : buffs) {
                if (event.side == LogicalSide.SERVER) {
                    buff.serverTick((ServerPlayerEntity) event.player, ((ServerPlayerEntity) event.player).getLevel());
                } else {
                    buff.clientTick((ClientPlayerEntity) event.player, ((ClientPlayerEntity) event.player).clientLevel);
                }
            }
            ((IPlayerEntityMixin) event.player).getAdditionalCooldowns().tick();
            if (event.side == LogicalSide.SERVER) {
                SoulPollutionHandler.mayPollute(event.player, event.player.level, SoulPollutionHandler.POLLUTE_PROBABILITY);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!event.getEntityLiving().level.isClientSide) {
            ((IEntityMixin) event.getEntityLiving()).setReversed(event.getEntityLiving().hasEffect(ModEffects.REVERSION));
        }
        if (event.getEntityLiving() instanceof IShieldUser && ((IShieldUser) event.getEntityLiving()).getSharedShield() != null) {
//          Multi-tick is not allowed for shields
            if (!(event.getEntityLiving() instanceof ISharedShieldUser) || (((ISharedShieldUser) event.getEntityLiving()).getShieldProvider() == event.getEntityLiving())) {
                ((IShieldUser) event.getEntityLiving()).getSharedShield().tick();
            }
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

    @SubscribeEvent
    public static void onCalculatingLootLevel(LootingLevelEvent event) {
        if (event.getDamageSource().getEntity() instanceof PlayerEntity && ExtraAbility.PILLAGER.isOn((PlayerEntity) event.getDamageSource().getEntity())) {
            event.setLootingLevel(event.getLootingLevel() + ExtraAbilityConstants.PILLAGER_LOOTING_LEVEL_BONUS);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoadWithHighPriority(BiomeLoadingEvent event) {
        if (isVanillaRegistryName(event.getName()) && "soul_sand_valley".equals(event.getName().getPath())) {
            event.getSpawns().addMobCharge(ModEntities.SOUL_SKELETON, 0.7, 0.15);
            event.getSpawns().addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 20, 5, 5));
        }
        if (isVanillaRegistryName(event.getName()) && "warped_forest".equals(event.getName().getPath())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_POISONOUS_FIRE);
        }
    }

    @SubscribeEvent
    public static void onBiomeLoad(BiomeLoadingEvent event) {
        if (isVanillaRegistryName(event.getName()) && "soul_sand_valley".equals(event.getName().getPath())) {
            ((MobSpawnInfoAccessor) event.getSpawns()).getSpawners().get(EntityClassification.MONSTER).removeIf(spawners -> spawners.type == EntityType.SKELETON);
            ((MobSpawnInfoAccessor) event.getSpawns()).getMobSpawnCosts().remove(EntityType.SKELETON);
        }
    }

    private static boolean isVanillaRegistryName(@Nullable ResourceLocation location) {
        return location != null && "minecraft".equals(location.getNamespace());
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
        AdditionalCooldownTracker tracker = oldPlayerM.getAdditionalCooldowns();
        newPlayerM.getAdditionalCooldowns().reloadFrom(tracker.save());
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

    @SubscribeEvent
    public static void onCalculatingLivingVisibility(LivingEvent.LivingVisibilityEvent event) {
        if (event.getLookingEntity() != null) {
            ItemStack stack = event.getEntityLiving().getItemBySlot(EquipmentSlotType.HEAD);
            Item item = stack.getItem();
            EntityType<?> entityType = event.getLookingEntity().getType();
            if (ModSkulls.matches(entityType, item)) {
                event.modifyVisibility(0.5);
            }
            if (event.getEntityLiving() instanceof PlayerEntity && ExtraAbility.IMITATOR.isOn((PlayerEntity) event.getEntityLiving())) {
                event.modifyVisibility(ExtraAbilityConstants.IMITATOR_VISIBILITY_MODIFIER);
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
                .flatMap(manager -> StreamSupport.stream(manager.spliterator(), false))
                .filter(challenge -> challenge.hasChallenger(player))
                .filter(challenge -> !onlyStrictChallenges || challenge.isStrict())
                .forEach(challenge -> challenge.lose(reason, reason, null));
    }
}
