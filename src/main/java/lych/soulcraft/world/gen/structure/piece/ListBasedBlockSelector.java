package lych.soulcraft.world.gen.structure.piece;

import lych.soulcraft.util.CollectionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.structure.StructurePiece;

import java.util.List;
import java.util.Random;

public class ListBasedBlockSelector extends StructurePiece.BlockSelector {
    private final List<BlockState> possibleStates;

    public ListBasedBlockSelector(List<BlockState> possibleStates) {
        this.possibleStates = possibleStates;
    }

    @Override
    public void next(Random random, int rx, int ry, int rz, boolean isEdge) {
        next = CollectionUtils.getRandom(possibleStates, random);
    }
}
