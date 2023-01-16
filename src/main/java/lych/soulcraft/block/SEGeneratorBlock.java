package lych.soulcraft.block;

import lych.soulcraft.api.ItemSEContainer;
import lych.soulcraft.block.entity.AbstractSEGeneratorTileEntity;
import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.item.SEStorageBlockItem;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.List;
import java.util.function.Supplier;

public class SEGeneratorBlock extends SimpleTileEntityBlock {
    public SEGeneratorBlock(Properties properties, Supplier<? extends AbstractSEGeneratorTileEntity> supplier) {
        super(properties, supplier);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult ray) {
        if (!world.isClientSide() && hand == Hand.MAIN_HAND) {
            TileEntity entity = world.getBlockEntity(pos);
            if (entity instanceof AbstractSEGeneratorTileEntity) {
                AbstractSEGeneratorTileEntity segen = (AbstractSEGeneratorTileEntity) entity;
                ItemStack itemInHand = player.getItemInHand(hand);
                ItemStack inside = segen.getSEStorageInside();
                if (player.isShiftKeyDown()) {
                    if (itemInHand.getItem() instanceof ItemSEContainer) {
                        segen.getInventory().setItem(0, itemInHand);
                        player.setItemInHand(hand, inside);
                    } else if (!inside.isEmpty() && itemInHand.isEmpty()) {
                        player.setItemInHand(hand, inside);
                        segen.getInventory().setItem(0, ItemStack.EMPTY);
                    }
                }
                NetworkHooks.openGui((ServerPlayerEntity) player, segen, packerBuffer -> packerBuffer.writeBlockPos(entity.getBlockPos()));
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        TileEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractSEGeneratorTileEntity) {
            AbstractSEGeneratorTileEntity segen = (AbstractSEGeneratorTileEntity) blockEntity;
            List<ItemStack> itemsInside = segen.getItemsInside();
            if (!world.isClientSide) {
                if (!player.isCreative() || ConfigHelper.canSEBlocksLoot() && segen.getSoulEnergyStored() > 0) {
                    ItemStack stack = new ItemStack(this);
                    stack.getOrCreateTag().putInt(SEStorageBlockItem.ENERGY_TAG, segen.getSoulEnergyStored());
                    EntityUtils.spawnItem(world, pos, stack);
                }
                for (ItemStack itemInside : itemsInside) {
                    EntityUtils.spawnItem(world, pos, itemInside);
                }
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }
}
