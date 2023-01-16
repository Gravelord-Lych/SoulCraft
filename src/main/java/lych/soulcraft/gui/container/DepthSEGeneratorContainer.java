package lych.soulcraft.gui.container;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DepthSEGeneratorContainer extends AbstractSEGeneratorContainer {
    public DepthSEGeneratorContainer(int id, BlockPos pos, PlayerInventory inventory, World world, IIntArray seProgress, IWorldPosCallable access) {
        super(ModContainers.DEPTH_SEGEN, id, pos, inventory, world, seProgress, access);
    }

    @Override
    protected Iterable<Block> getBlocks() {
        return ImmutableList.of(ModBlocks.DEPTH_SEGEN, ModBlocks.DEPTH_SEGEN_II);
    }
}
