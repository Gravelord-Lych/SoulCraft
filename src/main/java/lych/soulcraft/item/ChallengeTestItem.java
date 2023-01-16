package lych.soulcraft.item;

import lych.soulcraft.world.event.challenge.Challenge;
import lych.soulcraft.world.event.challenge.ChallengeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

//TODO: Remove
public class ChallengeTestItem extends Item {
    public ChallengeTestItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (world instanceof ServerWorld) {
            Challenge.createFor(player.isShiftKeyDown() ? ChallengeType.HUNTING : ChallengeType.UNDEAD_SURVIVAL, (ServerWorld) world, player.blockPosition(), (ServerPlayerEntity) player);
        }
        return super.use(world, player, hand);
    }
}
