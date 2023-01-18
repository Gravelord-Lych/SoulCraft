package lych.soulcraft.mixin;

import lych.soulcraft.util.mixin.IAbstractFireBlockMixin;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin implements IAbstractFireBlockMixin {
    @Shadow
    @Final
    private float fireDamage;

    @Override
    public float getFireDamage() {
        return fireDamage;
    }

    @Redirect(method = "entityInside", at = @At(value = "FIELD", target = "Lnet/minecraft/block/AbstractFireBlock;fireDamage:F"))
    private float hurtEntityInside(AbstractFireBlock instance, BlockState state, World world, BlockPos pos, Entity entity) {
        return ((IAbstractFireBlockMixin) instance).getFireType().getFireDamage(entity, world);
    }

    @Inject(method = "entityInside", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;hurt(Lnet/minecraft/util/DamageSource;F)Z", shift = At.Shift.AFTER))
    private void handleEntityInside(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        getFireType().entityInsideFire(state, world, pos, entity);
    }
}
