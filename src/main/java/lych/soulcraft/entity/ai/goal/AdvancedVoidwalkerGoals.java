package lych.soulcraft.entity.ai.goal;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.entity.monster.voidwalker.AbstractVoidwalkerEntity;
import lych.soulcraft.entity.monster.voidwalker.EtheArmorerAttackType;
import lych.soulcraft.entity.monster.voidwalker.EtheArmorerEntity;
import lych.soulcraft.entity.monster.voidwalker.VoidwalkerTier;
import lych.soulcraft.extension.soulpower.reinforce.Reinforcement;
import lych.soulcraft.extension.soulpower.reinforce.ReinforcementHelper;
import lych.soulcraft.util.*;
import lych.soulcraft.util.redirectable.PredicateRedirectable;
import lych.soulcraft.util.redirectable.Redirectable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * For non-{@link AbstractVoidwalkerEntity#isPrimary() primary} voidwalkers
 */
public final class AdvancedVoidwalkerGoals {
    private AdvancedVoidwalkerGoals() {}

    public static class FindAttackTypeGoal extends Goal {
        private final EtheArmorerEntity armorer;
        private EtheArmorerAttackType attackType;

        public FindAttackTypeGoal(EtheArmorerEntity armorer) {
            this.armorer = armorer;
        }

        @Override
        public boolean canUse() {
            if (armorer.getAttackType() == null && EntityUtils.isAlive(armorer.getTarget())) {
                attackType = armorer.findAttackType(armorer.getTarget());
                if (attackType != null) {
                    return true;
                }
                armorer.setTarget(null);
                return false;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            armorer.setAttackType(attackType);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }
    }

    public static abstract class EtheArmorerGoal extends Goal {
        protected final EtheArmorerEntity armorer;
        protected final Random random;
        protected final double speedModifier;
        protected LivingEntity target;
        private int maxAttackTime;
        private int attackIntervalTicks;
        private int attackTicks;
        private int attackTime;

        protected EtheArmorerGoal(EtheArmorerEntity armorer, double speedModifier) {
            this.armorer = armorer;
            this.random = armorer.getRandom();
            this.speedModifier = speedModifier;
            setFlags(makeFlags());
            Objects.requireNonNull(getAttackType());
        }

        protected abstract EnumSet<Flag> makeFlags();

        @Override
        public boolean canUse() {
            if (armorer.getAttackType() != getAttackType()) {
                return false;
            }
            LivingEntity target = armorer.getTarget();
            if (EntityUtils.isAlive(target)) {
                this.target = target;
                return true;
            }
            return false;
        }

        @Override
        public void start() {
            super.start();
            maxAttackTime = getMaxAttackTime();
            attackTicks = getAttackDuration();
            armorer.setAttackType(getAttackType());
            armorer.setLaserTarget(target);
        }

        @Override
        public void tick() {
            super.tick();
            armorer.getLookControl().setLookAt(target, 30, 30);
            if (armorer.distanceToSqr(target) > getAttackRadius() * getAttackRadius()) {
                armorer.getNavigation().moveTo(target, speedModifier);
            } else {
                armorer.getNavigation().stop();
            }
            if (attackIntervalTicks > 0) {
                attackIntervalTicks--;
                if (attackIntervalTicks == 0) {
                    armorer.setLaserTarget(target);
                    attackTicks = getAttackDuration();
                }
                return;
            }
            if (attackTicks > 0) {
                attackTicks--;
            } else {
                if (performAttack()) {
                    attackTime++;
                    if (attackTime >= maxAttackTime) {
                        attackTicks = -1;
                    } else {
                        armorer.setLaserTarget(null);
                        attackIntervalTicks = getAttackInterval();
                    }
                } else {
                    attackTicks = -1;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && attackTicks >= 0;
        }

        @Override
        public void stop() {
            super.stop();
            maxAttackTime = 0;
            attackTicks = 0;
            attackTime = 0;
            attackIntervalTicks = 0;
            target = null;
            armorer.setLaserTarget(null);
            armorer.setAttackType(null);
        }

        protected Pair<EquipmentSlotType, ItemStack> pickItem(Predicate<? super ItemStack> predicate) {
            return pickItem(predicate, 0.5);
        }

        protected Pair<EquipmentSlotType, ItemStack> pickItem(Predicate<? super ItemStack> predicate, double pickArmorProbability) {
            List<ItemStack> armors = CollectionUtils.list(target.getArmorSlots());
            List<Pair<EquipmentSlotType, ItemStack>> armorPairs = armors.stream().filter(predicate).map(stack -> Pair.of(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.ARMOR, armors.indexOf(stack)), stack)).collect(Collectors.toList());
            List<ItemStack> items = CollectionUtils.list(target.getHandSlots());
            List<Pair<EquipmentSlotType, ItemStack>> itemPairs = items.stream().filter(predicate).map(stack -> Pair.of(EquipmentSlotType.byTypeAndIndex(EquipmentSlotType.Group.HAND, items.indexOf(stack)), stack)).collect(Collectors.toList());
            boolean pickArmor = !armorPairs.isEmpty() && random.nextDouble() < pickArmorProbability;
            return Utils.getOrDefault(pickArmor ? CollectionUtils.getNonnullRandom(armorPairs, random) : CollectionUtils.getRandom(itemPairs, random), Pair.of(EquipmentSlotType.MAINHAND, ItemStack.EMPTY));
        }

        protected abstract boolean performAttack();

        protected abstract int getAttackDuration();

        protected int getAttackInterval() {
            return 20;
        }

        protected double getAttackRadius() {
            return 12;
        }

        protected abstract int getMaxAttackTime();

        protected abstract EtheArmorerAttackType getAttackType();
    }

    public static class ReinforceFriendlyGoal extends EtheArmorerGoal {
        private static final double SKIP_ENCHANTMENT_PROBABILITY = 0.5;

        public ReinforceFriendlyGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack stack = pickItem(this::applicable).getSecond();
            if (stack.isEmpty()) {
                return false;
            }
            for (int i = 0; i < getApplyCount(); i++) {
                stack = applyEnchantmentChanges(stack);
            }
            return stack.isEnchanted();
        }

        protected boolean applicable(ItemStack stack) {
            return stack.isEnchantable() || stack.isEnchanted();
        }

        protected ItemStack applyEnchantmentChanges(ItemStack stack) {
            boolean shouldEnchant = true;
            if (stack.isEnchanted()) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                    if (entry.getKey().getMaxLevel() > entry.getValue() && random.nextDouble() >= SKIP_ENCHANTMENT_PROBABILITY) {
                        entry.setValue(entry.getValue() + 1);
                        shouldEnchant = false;
                    }
                }
                if (!shouldEnchant) {
                    EnchantmentHelper.setEnchantments(enchantments, stack);
                }
            }
            if (alwaysEnchant() || shouldEnchant) {
                stack = EnchantmentHelper.enchantItem(random, stack, getEnchantLevel(), false);
            }
            return stack;
        }

        private int getApplyCount() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 3;
                case ELITE:
                    return 2;
                default:
                    return 1;
            }
        }

        private boolean alwaysEnchant() {
            return armorer.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY);
        }

        private int getEnchantLevel() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 30;
                case ELITE:
                    return 20;
                case EXTRAORDINARY:
                    return 12;
                default:
                    return 6;
            }
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 2;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.REINFORCE;
        }
    }

    public static class DamageWeaponGoal extends EtheArmorerGoal {
        private static final double DAMAGE_INVENTORY_ITEM_PROBABILITY = 0.1;
        private static final int DAMAGE_COUNT = 4;

        public DamageWeaponGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            Pair<EquipmentSlotType, ItemStack> pair = getDamageablePair();
            ItemStack stack = pair.getSecond();
            if (stack.isEmpty()) {
                return false;
            }
            int oldDamage = stack.getDamageValue();
            for (int i = 0; i < DAMAGE_COUNT; i++) {
                boolean destroyed = damage(stack, pair.getFirst(), getDamageAmount(stack));
                if (destroyed) {
                    if (pair.getFirst() != null && !(target instanceof PlayerEntity)) {
                        target.setItemSlot(pair.getFirst(), ItemStack.EMPTY);
                    }
                    break;
                }
            }
            int newDamage = stack.getDamageValue();
            return newDamage > oldDamage;
        }

        private Pair<EquipmentSlotType, ItemStack> getDamageablePair() {
            Pair<EquipmentSlotType, ItemStack> pair = pickItem(ItemStack::isDamageableItem);
            ItemStack stack = pair.getSecond();
            List<ItemStack> inventoryItems = InventoryUtils.getInventoryItemsIfIsPlayer(target);
            inventoryItems.removeIf(stackIn -> !stackIn.isDamageableItem());
            boolean fromInventory = false;
            if (stack.isEmpty() || !inventoryItems.isEmpty() && random.nextDouble() < DAMAGE_INVENTORY_ITEM_PROBABILITY) {
                stack = Utils.getOrDefault(CollectionUtils.getRandom(inventoryItems, random), ItemStack.EMPTY);
                fromInventory = true;
            }
            return Pair.of(fromInventory ? null : pair.getFirst(), stack);
        }

        private int getDamageAmount(ItemStack stack) {
            return Utils.randomlyCast(getBaseDamageAmount() * getDamageAmountMultiplier(stack.getMaxDamage()), random);
        }

        private int getBaseDamageAmount() {
            switch (armorer.getTier()) {
                case PARAGON:
                    return 8;
                case ELITE:
                    return 4;
                case EXTRAORDINARY:
                    return 2;
                default:
                    return 1;
            }
        }

        private double getDamageAmountMultiplier(int maxDamage) {
            double min = 1;
            if (maxDamage <= 0) {
                return min;
            }
            return Math.max(min, Math.log(maxDamage)) * 0.5;
        }

        private boolean damage(ItemStack stack, @Nullable EquipmentSlotType type, int amount) {
            if (target instanceof PlayerEntity) {
                stack.hurtAndBreak(amount, target, type == null ? DefaultValues.dummyConsumer() : entity -> entity.broadcastBreakEvent(type));
                return stack.isEmpty();
            }
            return random.nextDouble() < Math.log10(1 + getBaseDamageAmount());
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1 + random.nextInt(2);
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.DAMAGE;
        }
    }

    public static class ReconstructWeaponGoal extends EtheArmorerGoal {
        public ReconstructWeaponGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            if (target instanceof AbstractVoidwalkerEntity) {
                target.setItemInHand(Hand.MAIN_HAND, ((AbstractVoidwalkerEntity) target).createWeapon());
            } else if (ConfigHelper.shouldFailhard()) {
                throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + String.format("The Ethe-Armorer's reconstruct-target(%s) is not a voidwalker. Why?", target.getType().getRegistryName()));
            }
            return !target.getMainHandItem().isEmpty();
        }

        @Override
        protected int getAttackDuration() {
            return 40;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.RECONSTRUCT;
        }
    }

    public static class RandomlyRenameGoal extends EtheArmorerGoal {
        public RandomlyRenameGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack stack = pickItem(EtheArmorerEntity::renameable).getSecond();
            stack.setHoverName(new StringTextComponent(String.valueOf(generateRandomNumber())));
            return !stack.isEmpty();
        }

        private int generateRandomNumber() {
            if (random.nextDouble() < 0.01) {
                return 23333;
            }
            int min = 10000;
            int max = 99999;
            return MathHelper.nextInt(random, min, max);
        }

        @Override
        protected int getAttackDuration() {
            return 20;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.RENAME;
        }
    }

    public static class DisenchantAndCurseGoal extends ReinforceFriendlyGoal {
        private static final double SKIP_PROBABILITY = 0.5;
        private static final double APPLY_TO_REINFORCEMENTS_PROBABILITY = 0.3;
        private static final double ADD_CURSE_PROBABILITY = 0.25;

        public DisenchantAndCurseGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected boolean applicable(ItemStack stack) {
            return stack.isEnchanted() || ReinforcementHelper.hasReinforcements(stack);
        }

        @Override
        protected ItemStack applyEnchantmentChanges(ItemStack stack) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            boolean noValidEnchantments = enchantments.isEmpty() || enchantments.keySet().stream().allMatch(Enchantment::isCurse);
            if (noValidEnchantments || random.nextDouble() < APPLY_TO_REINFORCEMENTS_PROBABILITY) {
                Map<Reinforcement, Integer> reinforcements = ReinforcementHelper.getReinforcements(stack);
                for (Iterator<Map.Entry<Reinforcement, Integer>> itr = reinforcements.entrySet().iterator(); itr.hasNext(); ) {
                    Map.Entry<Reinforcement, Integer> entry = itr.next();
                    if (!itr.hasNext() || random.nextDouble() >= SKIP_PROBABILITY) {
                        if (entry.getValue() > 1) {
                            entry.setValue(entry.getValue() - 1);
                        } else {
                            itr.remove();
                        }
                    }
                }
                ReinforcementHelper.putReinforcements(stack, reinforcements, true);
            } else {
                for (Iterator<Map.Entry<Enchantment, Integer>> itr = enchantments.entrySet().iterator(); itr.hasNext(); ) {
                    Map.Entry<Enchantment, Integer> entry = itr.next();
                    if (!entry.getKey().isCurse() && (!itr.hasNext() || random.nextDouble() >= SKIP_PROBABILITY)) {
                        if (entry.getValue() > 1) {
                            entry.setValue(entry.getValue() - 1);
                        } else {
                            itr.remove();
                        }
                    }
                }
                EnchantmentHelper.setEnchantments(enchantments, stack);
            }
            if (random.nextDouble() < ADD_CURSE_PROBABILITY) {
                Enchantment curse = CollectionUtils.getRandom(Streams.stream(ForgeRegistries.ENCHANTMENTS).filter(Enchantment::isCurse).filter(enchantment -> enchantment.canEnchant(stack)).filter(enchantment -> EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) <= 0).collect(Collectors.toList()), random);
                if (curse == null) {
                    return stack;
                }
                stack.enchant(curse, EnchantmentHelper.getItemEnchantmentLevel(curse, stack) + 1);
            }
            return stack;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.CURSE;
        }
    }

    public static class WoodifyMainHandItemGoal extends EtheArmorerGoal {
        public static final List<PredicateRedirectable<ItemStack, ItemStack>> REDIRECTABLES = new ArrayList<>();
        private static final int DURABILITY_REMAINING = 10;

        static {
            PredicateRedirectable.Creator<ItemStack> planks = PredicateRedirectable.withFactory(() -> new ItemStack(Items.OAK_PLANKS));
            PredicateRedirectable.Creator<ItemStack> stick = PredicateRedirectable.withFactory(() -> new ItemStack(Items.STICK));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_SWORD), stack -> stack.getItem() instanceof SwordItem && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_PICKAXE), stack -> stack.getToolTypes().contains(ToolType.PICKAXE) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_AXE), stack -> stack.getToolTypes().contains(ToolType.AXE) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_SHOVEL), stack -> stack.getToolTypes().contains(ToolType.SHOVEL) && nonWood(stack)));
            REDIRECTABLES.add(PredicateRedirectable.createDirectly(() -> new ItemStack(Items.WOODEN_HOE), stack -> stack.getToolTypes().contains(ToolType.HOE) && nonWood(stack)));
            REDIRECTABLES.add(stick.using(ItemStack::isDamageableItem));
            REDIRECTABLES.add(planks.using(DefaultValues::always));
        }

        public WoodifyMainHandItemGoal(EtheArmorerEntity armorer, double speedModifier) {
            super(armorer, speedModifier);
        }

        @Override
        protected EnumSet<Flag> makeFlags() {
            return EnumSet.of(Flag.MOVE, Flag.LOOK);
        }

        @Override
        protected boolean performAttack() {
            ItemStack mainHandItem = target.getMainHandItem();
            if (EtheArmorerEntity.isWood(mainHandItem)) {
                return false;
            }
            ItemStack wood = ItemStack.EMPTY;
            for (Redirectable<ItemStack, ItemStack> redirectable : REDIRECTABLES) {
                if (redirectable.test(mainHandItem)) {
                    wood = redirectable.redirect(mainHandItem);
                    copyTagAndResetDamage(mainHandItem, wood);
                    break;
                }
            }
            if (wood.isEmpty() && ConfigHelper.shouldFailhard()) {
                throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "Wood not found, that should be impossible");
            }
            target.setItemInHand(Hand.MAIN_HAND, wood);
            return !target.getMainHandItem().isEmpty();
        }

        private static void copyTagAndResetDamage(ItemStack mainHandItem, ItemStack wood) {
            wood.setCount(mainHandItem.getCount());
            if (wood.isDamageableItem()) {
                wood.setTag(mainHandItem.getTag());
                wood.setDamageValue(wood.getMaxDamage() - DURABILITY_REMAINING);
            }
        }

        private static boolean nonWood(ItemStack stack) {
            return !(stack.getItem() instanceof TieredItem) || ((TieredItem) stack.getItem()).getTier() != ItemTier.WOOD;
        }

        @Override
        protected int getAttackDuration() {
            return armorer.getTier().strongerThan(VoidwalkerTier.EXTRAORDINARY) ? 40 : 80;
        }

        @Override
        protected int getMaxAttackTime() {
            return 1;
        }

        @Override
        protected EtheArmorerAttackType getAttackType() {
            return EtheArmorerAttackType.WOODIFY;
        }
    }
}
