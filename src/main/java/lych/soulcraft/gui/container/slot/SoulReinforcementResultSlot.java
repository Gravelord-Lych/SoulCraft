package lych.soulcraft.gui.container.slot;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcement;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcements;
import lych.soulcraft.gui.container.inventory.SoulReinforcementTableIngredientInventory;
import lych.soulcraft.item.SoulContainerItem;
import lych.soulcraft.util.SoulEnergies;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class SoulReinforcementResultSlot extends Slot {
    private final SoulReinforcementTableIngredientInventory ingredientSlots;

    public SoulReinforcementResultSlot(IInventory inventory, SoulReinforcementTableIngredientInventory ingredientSlots, int index, int x, int y) {
        super(inventory, index, x, y);
        this.ingredientSlots = ingredientSlots;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public ItemStack onTake(PlayerEntity player, ItemStack stack) {
        ItemStack seContainer = ingredientSlots.getItem(2);
        consume(stack, seContainer);
        ingredientSlots.setItem(0, ItemStack.EMPTY);

        return super.onTake(player, stack);
    }

    public void consume(ItemStack result, ItemStack seContainer) {
        EntityType<?> type = SoulContainerItem.getType(ingredientSlots.getItem(1));
        if (type != null) {
            Reinforcement reinforcement = Reinforcements.get(type);
            if (reinforcement != null) {
                int oldLevel = reinforcement.getLevel(ingredientSlots.getItem(0));
                int newLevel = reinforcement.getLevel(result);
                int soulContainerCost = reinforcement.getCost(oldLevel, newLevel);
                if (ingredientSlots.getItem(1).getCount() < soulContainerCost) {
                    throw new AssertionError();
                }
                ItemStack remainder;
                if (ingredientSlots.getItem(1).getCount() == soulContainerCost) {
                    remainder = ItemStack.EMPTY;
                } else {
                    remainder = ingredientSlots.getItem(1).copy();
                    remainder.shrink(soulContainerCost);
                }
                ingredientSlots.setItem(1, remainder);

                int energyCost = Reinforcements.getEnergyCost(reinforcement, oldLevel, newLevel);
                if (SoulEnergies.of(seContainer).orElseThrow(AssertionError::new).getSoulEnergyStored() < energyCost) {
                    throw new AssertionError();
                }

                SoulEnergies.costSimply(ImmutableList.of(seContainer), energyCost);
                return;
            }
        }
        throw new AssertionError();
    }
}
