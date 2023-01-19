package lych.soulcraft.api;

import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.api.exa.MobDebuff;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.api.shield.ISharedShield;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

final class SoulCraftAPIDummyImpl implements SoulCraftAPI {
    SoulCraftAPIDummyImpl() {}

    @Nullable
    @Override
    public IExtraAbility createExtraAbility(ResourceLocation registryName, int cost, boolean special) {
        return null;
    }

    @Override
    public Optional<IExtraAbility> getExtraAbilityByRegistryName(ResourceLocation registryName) {
        return Optional.empty();
    }

    @Override
    public Set<IExtraAbility> getExtraAbilitiesOnPlayer(PlayerEntity player) {
        return Collections.emptySet();
    }

    @Override
    public void registerExtraAbility(@Nullable IExtraAbility exa) {}

    @Override
    public Map<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities() {
        return Collections.emptyMap();
    }

    @Override
    public void registerSoulCaveCarverReplaceableBlock(Block block) {}

    @Override
    public boolean isDummy() {
        return true;
    }

    @Nullable
    @Override
    public ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, Supplier<ItemStack> stack) {
        return null;
    }

    @Nullable
    @Override
    public PlayerBuff bind(IExtraAbility exa, PlayerBuff buff) {
        return null;
    }

    @Nullable
    @Override
    public MobDebuff bind(IExtraAbility exa, MobDebuff debuff) {
        return null;
    }

    @Nullable
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense) {
        return null;
    }

    @Nullable
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, boolean consumable) {
        return null;
    }

    @Nullable
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount) {
        return null;
    }

    @Nullable
    @Override
    public ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount, boolean consumable) {
        return null;
    }

    @Override
    public ISharedShield loadShield(CompoundNBT compoundNBT) {
        return null;
    }
}
