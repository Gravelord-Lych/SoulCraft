package lych.soulcraft.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @ModifyArg(method = "<clinit>",
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/block/Blocks;FIRE:Lnet/minecraft/block/Block;")),
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/block/Blocks;register(Ljava/lang/String;Lnet/minecraft/block/Block;)Lnet/minecraft/block/Block;",
                    ordinal = 0))
    private static Block modifySoulFireBlock(Block old) {
        return new FireBlock(AbstractBlock.Properties.copy(old));
    }
}
