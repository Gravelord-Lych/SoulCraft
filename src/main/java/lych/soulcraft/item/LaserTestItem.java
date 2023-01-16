package lych.soulcraft.item;

import lych.soulcraft.extension.laser.LaserAttackResult;
import lych.soulcraft.extension.laser.LaserData;
import lych.soulcraft.extension.laser.LaserSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

// TODO: Remove
@Deprecated
public class LaserTestItem extends Item {
    public LaserTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld && hand == Hand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            LaserSource src = LaserData.REDSTONE.create(new Vector3d(player.getX(), player.getEyeY(), player.getZ()), world);
            LaserAttackResult result = src.directlyAttack(player.getLookAngle().scale(1000), player);
            result.getPassedEntities().forEach(entity -> entity.hurt(DamageSource.playerAttack(player), 8));
            result.getHitBlockPos().forEach(pos -> world.destroyBlock(pos, true));
            return ActionResult.success(stack);
        }
        return super.use(world, player, hand);
    }
}
