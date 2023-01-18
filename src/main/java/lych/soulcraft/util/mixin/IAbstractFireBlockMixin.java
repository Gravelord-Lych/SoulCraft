package lych.soulcraft.util.mixin;

import lych.soulcraft.extension.fire.Fire;
import net.minecraft.block.Block;

public interface IAbstractFireBlockMixin {
    float getFireDamage();

    default Fire getFireType() {
        return Fire.byBlock((Block) this);
    }
}
