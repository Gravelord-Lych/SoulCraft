package lych.soulcraft.extension.soulpower.buff;

import com.google.common.collect.Streams;
import lych.soulcraft.api.exa.PlayerBuff;
import lych.soulcraft.extension.ExtraAbility;
import lych.soulcraft.util.ExtraAbilityConstants;
import lych.soulcraft.util.ImmutableEffectInstance;
import lych.soulcraft.util.Utils;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public enum GoldPreferenceBuff implements PlayerBuff {
    INSTANCE;

    @Override
    public void startApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void stopApplyingTo(PlayerEntity player, World world) {}

    @Override
    public void serverTick(ServerPlayerEntity player, ServerWorld world) {
        tick(player);
    }

    @Override
    public void clientTick(ClientPlayerEntity player, ClientWorld world) {
        tick(player);
    }

    private static void tick(PlayerEntity player) {
        if (shouldApplyGoldPreference(player)) {
            ExtraAbilityConstants.GOLD_PREFERENCE_EFFECTS.stream().map(ImmutableEffectInstance::copy).forEach(player::addEffect);
        }
    }

    private static boolean shouldApplyGoldPreference(PlayerEntity player) {
        return ExtraAbility.GOLD_PREFERENCE.isOn(player) && Streams.stream(player.getAllSlots()).anyMatch(GoldPreferenceBuff::isGold);
    }

    private static boolean isGold(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof TieredItem && ((TieredItem) item).getTier() == ItemTier.GOLD) {
            return true;
        }
        if (item instanceof ArmorItem && ((ArmorItem) item).getMaterial() == ArmorMaterial.GOLD) {
            return true;
        }
        return Utils.getRegistryName(item).getPath().contains("gold");
    }
}
