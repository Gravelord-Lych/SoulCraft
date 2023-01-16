package lych.soulcraft.entity.monster;

import lych.soulcraft.item.ModItems;
import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class SoulSkeletonEntity extends AbstractSkeletonEntity {
    public SoulSkeletonEntity(EntityType<? extends SoulSkeletonEntity> type, World world) {
        super(type, world);
    }

    public static AttributeModifierMap.MutableAttribute createAttributes() {
        return AbstractSkeletonEntity.createAttributes();
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.SKELETON_STEP;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        if (super.doHurtTarget(target)) {
            if (!target.fireImmune()) {
                target.setSecondsOnFire(5);
                ((IEntityMixin) target).setOnSoulFire(true);
            }
            return true;
        }
        return false;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance instance) {
        setItemSlot(EquipmentSlotType.MAINHAND, new ItemStack(random.nextBoolean() ? ModItems.REFINED_SOUL_METAL_SWORD : ModItems.SOUL_BOW));
    }

    @Override
    protected AbstractArrowEntity getArrow(ItemStack stack, float power) {
        AbstractArrowEntity arrow = ModItems.SOUL_ARROW.createArrow(level, stack, this);
        arrow.setEnchantmentEffectsFromEntity(this, power);
        if (stack.getItem() == Items.TIPPED_ARROW && arrow instanceof ArrowEntity) {
            ((ArrowEntity) arrow).setEffectsFromItem(stack);
        }
        arrow.setSecondsOnFire(100);
        ((IEntityMixin) arrow).setOnSoulFire(true);
        return arrow;
    }

    @Override
    protected boolean isSunBurnTick() {
        return false;
    }
}
