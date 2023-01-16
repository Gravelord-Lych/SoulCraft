package lych.soulcraft.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

import java.util.Set;

public class WitherReinforcement extends TickableReinforcement {
    public WitherReinforcement() {
        super(EntityType.WITHER);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return false;
    }

    @Override
    protected void onLivingTick(ItemStack stack, LivingEntity entity, int level) {

    }

    @Override
    protected void onPlayerTick(ItemStack stack, PlayerEntity player, LogicalSide side, int level) {

    }

    @Override
    protected Set<EquipmentSlotType> getAvailableSlots() {
        return null;
    }
}
