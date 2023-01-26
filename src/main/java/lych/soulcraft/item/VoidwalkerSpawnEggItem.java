package lych.soulcraft.item;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.ModEntityNames;
import lych.soulcraft.entity.monster.voidwalker.VoidwalkerTier;
import lych.soulcraft.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class VoidwalkerSpawnEggItem extends ForgeSpawnEggItem {
    private static final ITextComponent SET_TIER = new TranslationTextComponent(SoulCraft.prefixMsg("change_voidwalker_spawn_egg_tier"));
    private static final String TAG = Utils.snakeToCamel(ModEntityNames.VOIDWALKER + ModItems.SPAWN_EGG_SUFFIX) + ModItems.TAG + VoidwalkerTier.class.getSimpleName();
    private static VoidwalkerTier currentTier;

    public VoidwalkerSpawnEggItem(Supplier<? extends EntityType<?>> type, int backgroundColor, int highlightColor, Properties props) {
        super(type, backgroundColor, highlightColor, props);
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return max(super.getRarity(stack), getTier(stack).getRarity());
    }

    private static Rarity max(Rarity a, Rarity b) {
        return a.ordinal() >= b.ordinal() ? a : b;
    }

    public static VoidwalkerTier getCurrentTier() {
        return currentTier;
    }

    public static void setCurrentTier(@Nullable VoidwalkerTier currentTier) {
        VoidwalkerSpawnEggItem.currentTier = currentTier;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (player.isShiftKeyDown() && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            setTier(stack, getTier(stack).next());
            if (!world.isClientSide()) {
                player.sendMessage(SET_TIER.copy().append(getTier(stack).getDescription(false)), Util.NIL_UUID);
            }
            return ActionResult.sidedSuccess(stack, world.isClientSide());
        }
        ItemStack stack = player.getItemInHand(hand);
        setCurrentTier(getTier(stack));
        ActionResult<ItemStack> result = super.use(world, player, hand);
        setCurrentTier(null);
        return result;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        setCurrentTier(getTier(context.getItemInHand()));
        ActionResultType type = super.useOn(context);
        setCurrentTier(null);
        return type;
    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        VoidwalkerTier type = getTier(stack);
        if (type.isOrdinary()) {
            return super.getName(stack);
        }
        return type.makeSpawnEggDescription(this);
    }

    public static VoidwalkerTier getTier(ItemStack stack) {
        if (!stack.hasTag()) {
            return VoidwalkerTier.ORDINARY;
        }
        return VoidwalkerTier.byId(stack.getOrCreateTag().getInt(TAG));
    }

    public static void setTier(ItemStack stack, VoidwalkerTier tier) {
        stack.getOrCreateTag().putInt(TAG, tier.getId());
    }
}
