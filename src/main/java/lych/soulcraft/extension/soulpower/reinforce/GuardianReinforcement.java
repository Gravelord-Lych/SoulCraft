package lych.soulcraft.extension.soulpower.reinforce;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Map;
import java.util.function.Supplier;

public class GuardianReinforcement extends DefensiveReinforcement {
    private static final float BASE_THORNS_DAMAGE = 1;
    private static final float THORNS_DAMAGE_STEP = 0.5f;

    public GuardianReinforcement() {
        super(EntityType.GUARDIAN);
    }

    protected GuardianReinforcement(EntityType<?> type) {
        super(type);
    }

    protected GuardianReinforcement(ResourceLocation typeName) {
        super(typeName);
    }

    protected GuardianReinforcement(Supplier<EntityType<?>> type) {
        super(type);
    }

    @Override
    protected boolean isItemPosSuitable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem && (((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.CHEST || ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.LEGS);
    }

    @Override
    protected void onAttack(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingAttackEvent event) {}

    @Override
    protected void onHurt(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingHurtEvent event) {
        if (source.getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) source.getEntity();
            attacker.hurt(DamageSource.thorns(entity), (BASE_THORNS_DAMAGE + level * THORNS_DAMAGE_STEP) * getThornsDamageMultiplier());
        }
    }

    protected float getThornsDamageMultiplier() {
        return 1;
    }

    @Override
    protected void onDamage(ItemStack stack, LivingEntity entity, DamageSource source, float amount, int level, LivingDamageEvent event) {}

    @Override
    protected boolean onlyCalculateOnce() {
        return true;
    }

    @Override
    public int getLevel(ItemStack stack) {
        if (stack.isEmpty()) {
            return 0;
        }
        return ReinforcementHelper.getReinforcements(stack).entrySet().stream().filter(e -> e.getKey() instanceof GuardianReinforcement).mapToInt(Map.Entry::getValue).sum();
    }
}
