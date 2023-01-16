package lych.soulcraft.util.mixin;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITridentEntityMixin {
    @OnlyIn(Dist.CLIENT)
    boolean isSoulFoil();
}
