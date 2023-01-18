package lych.soulcraft.util.mixin;

import lych.soulcraft.extension.fire.Fire;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.Optional;

public interface IEntityMixin {
    boolean isOnSoulFire();

    @OnlyIn(Dist.CLIENT)
    boolean displaySoulFireAnimation();

    @Deprecated
    void setOnSoulFire(boolean onSoulFire);

    boolean isReversed();

    void setReversed(boolean reversed);

    Optional<Color> getHighlightColor();

    void setHighlightColor(@Nullable Color highlightColor);

    boolean callGetSharedFlag(int flag);

    Fire getFireOnSelf();

    void setFireOnSelf(Fire fire);
}
