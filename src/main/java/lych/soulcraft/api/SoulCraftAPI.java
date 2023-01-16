package lych.soulcraft.api;

import lych.soulcraft.api.exa.IExtraAbility;
import lych.soulcraft.api.exa.MobDebuff;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.api.shield.ISharedShield;
import lych.soulcraft.world.gen.carver.SoulCaveCarver;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public interface SoulCraftAPI {
    String MOD_ID = "soulcraft";

    /**
     * @return An instance of this. {@link SoulCraftAPIDummyImpl} if SoulCraft Mod is not found
     */
    static SoulCraftAPI getInstance() {
        return APIConstants.INSTANCES.get();
    }

    /**
     * Get the version of {@link SoulCraftAPI}.
     * @return The version of the API
     */
    default int apiVersion() {
        return 1;
    }

    /**
     * Create an Extra Ability (for player). It was named "Extra" because there's a class called {@link net.minecraft.entity.player.PlayerAbilities PlayerAbilities}.
     * @param registryName The registry name of the Extra Ability
     * @return The Extra Ability created by the registry name. Null if the API is a dummy
     */
    @Nullable
    IExtraAbility createExtraAbility(ResourceLocation registryName);

    /**
     * Gets an Extra Ability from the registry name.
     * @param registryName The registry name of the Extra Ability
     */
    Optional<IExtraAbility> getExtraAbilityByRegistryName(ResourceLocation registryName);

    /**
     * Gets all the Extra Abilities on the player.
     * @param player The player
     * @return All the Extra Abilities on the player
     */
    Set<IExtraAbility> getExtraAbilitiesOnPlayer(PlayerEntity player);

    /**
     * Register an Extra Ability.
     * @param exa The Extra Ability that will be registered
     */
    void registerExtraAbility(@Nullable IExtraAbility exa);

    /**
     * Gets all registered Extra Abilities.
     * @return The Extra Abilities that were registered before the method was invoked. This map is <strong>unmodifiable</strong>
     */
    Map<ResourceLocation, IExtraAbility> getRegisteredExtraAbilities();

    /**
     * Register a block that entities inside it will be on soul fire. This overload is mainly used for {@link AbstractFireBlock fires} as the entities inside the fire will be certainly on common fire.
     * @param block The block which can cause soul fire
     */
    default void registerSoulFire(Block block) {
        registerSoulFire(block, -1);
    }

    /**
     * Register a block that entities inside it will be on soul fire.
     * @param block The block which can cause soul fire
     * @param secondsOnSoulFire How long will the fire last depends on it, if negative, the entity will not be ignited
     */
    void registerSoulFire(Block block, int secondsOnSoulFire);

    /**
     * Register a block that is replaceable and can be replaced by air when {@link SoulCaveCarver} carves.
     * @param block The replaceable block
     */
    void registerSoulCaveCarverReplaceableBlock(Block block);

    /**
     * @return True if the entity is on soul fire.
     */
    boolean isOnSoulFire(Entity entity);

    /**
     * Set the entity on soul fire or not on soul fire.
     * @param entity The entity
     */
    void setOnSoulFire(Entity entity, boolean onSoulFire);

    /**
     * @return True if the api is a dummy
     */
    boolean isDummy();

    @Nullable
    default ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, ItemStack stack) {
        return getSoulEnergyProviderForItem(container, () -> stack);
    }

    @Nullable
    ICapabilityProvider getSoulEnergyProviderForItem(ItemSEContainer container, Supplier<ItemStack> stack);

    /**
     * Bind an Extra Ability and a {@link PlayerBuff buff}
     * @return The previous buff associated with <code>exa</code>,  or <code>null</code> if there was no mapping for <code>exa</code>.
     */
    @Nullable
    PlayerBuff bind(IExtraAbility exa, PlayerBuff buff);

    /**
     * Bind an Extra Ability and a {@link MobDebuff debuff}
     * @return The previous debuff associated with <code>exa</code>,  or <code>null</code> if there was no mapping for <code>exa</code>.
     */
    @Nullable
    MobDebuff bind(IExtraAbility exa, MobDebuff debuff);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @return The newly created shared shield, <code>null</code> if Soul Craft is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param consumable Whether the shield {@link ISharedShield#canBeConsumed() can be consumed} or not
     * @return The newly created shared shield, <code>null</code> if Soul Craft is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, boolean consumable);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param maxRegenInterval The {@link ISharedShield#getMaxRegenInterval() max regenerate interval}
     *                         of the shield
     * @param regenAmount The {@link ISharedShield#getRegenAmount() amount of health regenerated} during a
     *                    shield regeneration
     * @return The newly created shared shield, <code>null</code> if Soul Craft is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount);

    /**
     * Creates a {@link ISharedShield shared shield}.
     * @param absoluteDefense The {@link ISharedShield#getAbsoluteDefense() AD} of the shield
     * @param passiveDefense The {@link ISharedShield#getPassiveDefense() PD} of the shield
     * @param maxRegenInterval The {@link ISharedShield#getMaxRegenInterval() max regenerate interval}
     *                         of the shield
     * @param regenAmount The {@link ISharedShield#getRegenAmount() amount of health regenerated} during a
     *                    shield regeneration
     * @param consumable Whether the shield {@link ISharedShield#canBeConsumed() can be consumed} or not
     * @return The newly created shared shield, <code>null</code> if Soul Craft is absent or broken
     */
    @Nullable
    ISharedShield createShield(float absoluteDefense, float passiveDefense, int maxRegenInterval, float regenAmount, boolean consumable);

    /**
     * Loads a shared shield from the specific {@link CompoundNBT NBT}.
     * @param compoundNBT THe NBT that stores the shield's data
     * @return The loaded shared shield, <code>null</code> if Soul Craft is absent or broken
     */
    @Nullable
    ISharedShield loadShield(CompoundNBT compoundNBT);
}
