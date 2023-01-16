package lych.soulcraft.mixin;

import lych.soulcraft.effect.ModEffects;
import lych.soulcraft.util.UltimateRaidUtils;
import lych.soulcraft.util.mixin.IRaidMixin;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.raid.Raid.WaveMember;
import net.minecraft.world.server.ServerBossInfo;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Random;

@Mixin(Raid.class)
public abstract class RaidMixin implements IRaidMixin {
    @Shadow @Final @Mutable private ServerBossInfo raidEvent;
    @Shadow @Final private static ITextComponent RAID_NAME_COMPONENT;
    @Shadow @Final private static ITextComponent RAID_BAR_VICTORY_COMPONENT;
    @Shadow @Final private static ITextComponent RAID_BAR_DEFEAT_COMPONENT;
    @Shadow @Final @Mutable private int numGroups;

    @Shadow private int groupsSpawned;
    @Shadow private float totalHealth;
    @Shadow @Final private ServerWorld level;

    @Shadow protected abstract boolean shouldSpawnBonusGroup();

    @Shadow protected abstract int getDefaultNumSpawns(WaveMember p_221330_1_, int p_221330_2_, boolean p_221330_3_);

    @Shadow protected abstract int getPotentialBonusSpawns(WaveMember p_221335_1_, Random p_221335_2_, int p_221335_3_, DifficultyInstance p_221335_4_, boolean p_221335_5_);

    @Shadow @Final private Random random;

    @Shadow public abstract void setLeader(int p_221324_1_, AbstractRaiderEntity p_221324_2_);

    @Shadow public abstract void joinRaid(int p_221317_1_, AbstractRaiderEntity p_221317_2_, @Nullable BlockPos p_221317_3_, boolean p_221317_4_);

    @Shadow public abstract int getNumGroups(Difficulty p_221306_1_);

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Shadow
    private Optional<BlockPos> waveSpawnPos;

    @Shadow public abstract void updateBossbar();

    @Shadow protected abstract void setDirty();

    @Shadow private int badOmenLevel;

    @Shadow @Final private static ITextComponent VICTORY;
    @Shadow @Final private static ITextComponent DEFEAT;

    private static final int ULRAID_NUM_GROUPS = 10;
    private static final ITextComponent ULRAID_NAME_COMPONENT = new TranslationTextComponent("event.soulcraft.ulraid");
    private static final ITextComponent ULRAID_BAR_VICTORY_COMPONENT = ULRAID_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final ITextComponent ULRAID_BAR_DEFEAT_COMPONENT = ULRAID_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);
    @Unique
    private boolean ultimate;

    @ModifyConstant(
            method = "tick",
            slice = @Slice(
                    from = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/raid/Raid;updateRaiders()V"
                    ),
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/raid/Raid;shouldSpawnGroup()Z"
                    )
            ),
            constant = @Constant(
                    intValue = 2,
                    ordinal = 0
            )
    )
    private int modifyShowRaiderCountThreshold(int showRaiderCountThreshold) {
        if (isUltimate()) {
            return 5;
        }
        return showRaiderCountThreshold;
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/server/ServerBossInfo;setName(Lnet/minecraft/util/text/ITextComponent;)V")
    )
    private void redirectName(ServerBossInfo bossInfo, ITextComponent component) {
        if (component == RAID_NAME_COMPONENT) {
            bossInfo.setName(isUltimate() ? ULRAID_NAME_COMPONENT : RAID_NAME_COMPONENT);
            return;
        }
        if (component == RAID_BAR_VICTORY_COMPONENT) {
            bossInfo.setName(isUltimate() ? ULRAID_BAR_VICTORY_COMPONENT : RAID_BAR_VICTORY_COMPONENT);
            return;
        }
        if (component == RAID_BAR_DEFEAT_COMPONENT) {
            bossInfo.setName(isUltimate() ? ULRAID_BAR_DEFEAT_COMPONENT : RAID_BAR_DEFEAT_COMPONENT);
            return;
        }
//      The input component must be "raiders_remaining" text now.
        IFormattableTextComponent component1 = (isUltimate() ? ULRAID_NAME_COMPONENT : RAID_NAME_COMPONENT).copy();
        component.getSiblings().forEach(component1::append);
        raidEvent.setName(component1);
    }

    @Override
    public boolean isUltimate() {
        return ultimate;
    }

    @Override
    public void setUltimate() {
        ultimate = true;
        numGroups = ULRAID_NUM_GROUPS;
        badOmenLevel = 5;
        raidEvent = new ServerBossInfo(ULRAID_NAME_COMPONENT, BossInfo.Color.RED, BossInfo.Overlay.NOTCHED_10);
    }

    @Inject(method = "save", at = @At(value = "HEAD"))
    private void saveUltimate(CompoundNBT compoundNBT, CallbackInfoReturnable<CompoundNBT> cir) {
        compoundNBT.putBoolean("Ultimate", isUltimate());
    }

    @Inject(
            method = "<init>(Lnet/minecraft/world/server/ServerWorld;Lnet/minecraft/nbt/CompoundNBT;)V",
            at = @At(value = "RETURN")
    )
    private void loadUltimate(ServerWorld world, CompoundNBT compoundNBT, CallbackInfo ci) {
        boolean ultimate = compoundNBT.getBoolean("Ultimate");
        if (ultimate) {
            setUltimate();
        }
    }

//  TODO use better mixin method
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/raid/Raid;spawnGroup(Lnet/minecraft/util/math/BlockPos;)V"))
    private void spawnCorrectGroup(Raid raid, BlockPos pos) {
        boolean hasLeader = false;
        int nextWave = groupsSpawned + 1;
        totalHealth = 0;
        DifficultyInstance difficulty = level.getCurrentDifficultyAt(pos);
        boolean shouldSpawnBonusGroup = shouldSpawnBonusGroup();

        for (WaveMember member : isUltimate() ? UltimateRaidUtils.getWaveMemberArray() : WaveMember.VALUES) {
            int spawnCount = getDefaultNumSpawns(member, nextWave, shouldSpawnBonusGroup) + (isUltimate() ?
                            getPotentialBonusSpawnsForUlraiders(member, random, nextWave, difficulty, shouldSpawnBonusGroup) :
                            getPotentialBonusSpawns(member, random, nextWave, difficulty, shouldSpawnBonusGroup));
            int passengerCount = 0;
            for (int i = 0; i < spawnCount; ++i) {
                AbstractRaiderEntity raider = ((WaveMemberAccessor) (Object) member).getEntityType().create(level);
                if (raider != null) {
                    if (!hasLeader && raider.canBeLeader()) {
                        raider.setPatrolLeader(true);
                        setLeader(nextWave, raider);
                        hasLeader = true;
                    }
                    if (isUltimate()) {
                        UltimateRaidUtils.applyUlraidBuffsTo(raider, member, difficulty, (Raid) (Object) this);
                    }
                    joinRaid(nextWave, raider, pos, false);
                    if (((WaveMemberAccessor) (Object) member).getEntityType() == EntityType.RAVAGER) {
                        AbstractRaiderEntity passenger = null;
                        if (nextWave == getNumGroups(Difficulty.NORMAL)) {
                            passenger = EntityType.PILLAGER.create(level);
                        } else if (nextWave >= getNumGroups(Difficulty.HARD)) {
                            if (passengerCount == 0) {
                                passenger = EntityType.EVOKER.create(level);
                            } else {
                                passenger = EntityType.VINDICATOR.create(level);
                            }
                        }
                        passengerCount++;
                        if (passenger != null) {
                            if (isUltimate()) {
                                UltimateRaidUtils.applyUlraidBuffsTo(passenger, member, difficulty, (Raid) (Object) this);
                            }
                            joinRaid(nextWave, passenger, pos, false);
                            passenger.moveTo(pos, 0, 0);
                            passenger.startRiding(raider);
                        }
                    }
                }
            }
        }

        waveSpawnPos = Optional.empty();
        groupsSpawned++;
        updateBossbar();
        setDirty();
    }

    @Inject(method = "absorbBadOmen", at = @At(value = "HEAD"), cancellable = true)
    private void clearCatastropheOmen(PlayerEntity player, CallbackInfo ci) {
        if (player.hasEffect(ModEffects.CATASTROPHE_OMEN)) {
            setUltimate();
            player.removeEffect(ModEffects.CATASTROPHE_OMEN);
            player.removeEffect(Effects.BAD_OMEN);
            ci.cancel();
        }
    }

    @Inject(method = "hasBonusWave", at = @At(value = "HEAD"), cancellable = true)
    private void noBonusWave(CallbackInfoReturnable<Boolean> cir) {
        if (isUltimate()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getNumGroups", at = @At(value = "HEAD"), cancellable = true)
    private void getCorrectNumGroups(Difficulty difficulty, CallbackInfoReturnable<Integer> cir) {
        if (isUltimate()) {
            cir.setReturnValue(ULRAID_NUM_GROUPS);
        }
    }

    private int getPotentialBonusSpawnsForUlraiders(WaveMember member, Random random, int nextWave, DifficultyInstance difficultyInstance, boolean shouldSpawnBonusGroup) {
        Difficulty difficulty = difficultyInstance.getDifficulty();
        boolean easy = difficulty == Difficulty.EASY;
        boolean normal = difficulty == Difficulty.NORMAL;
        int maxCount;
        if (member == UltimateRaidUtils.UL_WITCH) {
            if (easy || nextWave <= 3) {
                return 0;
            }
            maxCount = 1;
        } else if (member == UltimateRaidUtils.UL_PILLAGER || member == UltimateRaidUtils.UL_VINDICATOR) {
            if (easy) {
                maxCount = random.nextInt(2);
            } else if (normal) {
                maxCount = 1;
            } else {
                maxCount = 2;
            }
        } else if (member == UltimateRaidUtils.UL_RAVAGER) {
            maxCount = !easy && shouldSpawnBonusGroup ? 1 : 0;
        } else {
            return 0;
        }
        return maxCount > 0 ? random.nextInt(maxCount + 1) : 0;
    }
}
