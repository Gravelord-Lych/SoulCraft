package lych.soulcraft.block;

import lych.soulcraft.block.entity.GuiderTileEntity;
import lych.soulcraft.block.entity.ModTileEntities;

public class GuiderBlock extends SimpleTileEntityBlock {
    public GuiderBlock(Properties properties) {
        super(properties, () -> new GuiderTileEntity(ModTileEntities.GUIDER));
    }
}
