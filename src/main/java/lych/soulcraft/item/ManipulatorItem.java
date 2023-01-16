package lych.soulcraft.item;

import lych.soulcraft.entity.iface.IStrongMinded;
import lych.soulcraft.extension.highlight.HighlighterType;
import lych.soulcraft.extension.soulpower.control.ControlOptions;
import lych.soulcraft.extension.soulpower.control.ControlledMobData;
import lych.soulcraft.extension.soulpower.control.PresetControlFlags;
import lych.soulcraft.extension.soulpower.control.SoulManager;
import lych.soulcraft.extension.soulpower.control.controller.DefaultControllers;
import lych.soulcraft.util.InventoryUtils;
import lych.soulcraft.util.SoulEnergies;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.Optional;

public class ManipulatorItem extends Item {
    private static final String TAG = ModItemNames.MANIPULATOR + ".Tag.";
    private static final int COST = 1;
    private static final int FIND_TARGET_RANGE = 16;
    private static final int CLEAR_TARGET_RANGE = 24;

    public ManipulatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int index, boolean selected) {
        super.inventoryTick(stack, world, entity, index, selected);
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            if (InventoryUtils.isInHand(stack, player, selected)) {
                MobEntity mob = getVictim(stack, world);
                if (mob == null || mob.distanceToSqr(player) > CLEAR_TARGET_RANGE * CLEAR_TARGET_RANGE) {
                    findNewVictim(stack, world, player);
                }
                mob = getVictim(stack, world);
                if (mob != null && SoulEnergies.costSimply(player, COST)) {
                    control(stack, world, player);
                }
            }
        }
    }

    private static void findNewVictim(ItemStack stack, World world, LivingEntity controller) {
        EntityPredicate canControl = EntityPredicate.DEFAULT.range(FIND_TARGET_RANGE).selector(entity -> entity instanceof IMob && entity.canChangeDimensions() && !(entity instanceof IStrongMinded));
        Optional<MobEntity> victim = world.getNearbyEntities(MobEntity.class, canControl, controller, controller.getBoundingBox().inflate(FIND_TARGET_RANGE))
                .stream()
                .filter(Entity::isAlive)
                .min(Comparator.comparingDouble(mob -> mob.distanceToSqr(controller)));
        setVictim(stack, victim.orElse(null));
    }

    @Nullable
    private static MobEntity getVictim(ItemStack stack, World world) {
        int id = stack.getOrCreateTag().getInt(TAG + "Victim");
        if (id == -1) {
            return null;
        }
        Entity entity = world.getEntity(id);
        if (entity instanceof MobEntity) {
            return (MobEntity) entity;
        }
        return null;
    }

    private static void setVictim(ItemStack stack, @Nullable MobEntity mob) {
        stack.getOrCreateTag().putInt(TAG + "Victim", mob == null ? -1 : mob.getId());
    }

    private static void control(ItemStack stack, World world, LivingEntity controller) {
        MobEntity mob = getVictim(stack, world);
        if (mob != null && !world.isClientSide()) {
            ServerWorld serverWorld = (ServerWorld) world;
            SoulManager.get(serverWorld).control(mob,
                    ControlledMobData.builder()
                            .setMob(mob.getUUID())
                            .setBehaviorType(DefaultControllers.AUTO_ENEMY_CONTROL)
                            .setController(controller.getUUID())
                            .setControlTime(1)
                            .build(),
                    ControlOptions.builder()
                            .setFlags(PresetControlFlags.CONTROLLED_MONSTER)
                            .setHighlighterType(HighlighterType.NO_FLASH_SOUL_CONTROL)
                            .build());
        }
    }
}
