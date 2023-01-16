package lych.soulcraft.mixin;


import lych.soulcraft.util.SoulFireHelper;
import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public abstract class AbstractBlockMixin {
    @Inject(method = "entityInside", at = @At(value = "HEAD"))
    private void soulFire(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        if (/*!OptiFineHandler.isOptiFineLoaded() && */((AbstractBlock) (Object) this) instanceof Block && SoulFireHelper.isSoulFire((Block) (Object) this)) {
            ((IEntityMixin) entity).setOnSoulFire(true);
            int time = SoulFireHelper.getSoulFires().get((Block) (Object) this);
            if (time >= 0) {
                entity.setSecondsOnFire(time);
            }
        }
    }
}
