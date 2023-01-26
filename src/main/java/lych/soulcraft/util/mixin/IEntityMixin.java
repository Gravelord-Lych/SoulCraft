package lych.soulcraft.util.mixin;

import lych.soulcraft.extension.fire.Fire;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;

public interface IEntityMixin {
    void setOnSoulFire(boolean onSoulFire);

    boolean isReversed();

    void setReversed(boolean reversed);

    Optional<Color> getHighlightColor();

    void setHighlightColor(@Nullable Color highlightColor);

    boolean callGetSharedFlag(int flag);

    Fire getFireOnSelf();

    boolean doSetFireOnSelf(Fire fire);

    default void setFireOnSelf(Fire fire) {
        if (fire.isRealFire()) {
            Fire oldFire = getFireOnSelf();
            if (doSetFireOnSelf(fire)) {
                oldFire.stopApplyingTo((Entity) this, fire);
                fire.startApplyingTo((Entity) this, oldFire);
            }
        }
    }
}
