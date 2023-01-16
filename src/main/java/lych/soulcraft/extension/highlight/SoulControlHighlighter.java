package lych.soulcraft.extension.highlight;

import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Range;

import java.awt.*;
import java.util.UUID;

public class SoulControlHighlighter extends AbstractHighlighter {
    private static final Color SOUL_OUTLINE_COLOR = new Color(50, 230, 240);
    private static final Color MAX = new Color(100, 255, 255);
    private static final Color MIN = new Color(0, 205, 225);
    private static final Color SOUL_OUTLINE_COLOR_GLOWING = new Color(180, 255, 255);
    private static final Color MAX_GLOWING = new Color(230, 255, 255);
    private static final Color MIN_GLOWING = new Color(130, 255, 255);
    private static final int FLASH_THRESHOLD = 200;
    private static final int FLASH_FREQ = 10;

    public SoulControlHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SoulControlHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        Color color = getOriginalColor(entity);
        boolean glowing = color == SOUL_OUTLINE_COLOR_GLOWING;
        if (noFlash() || getHighlightTicks() > FLASH_THRESHOLD) {
            return color;
        }
        int flashTicks = (int) (FLASH_THRESHOLD - getHighlightTicks());
        float amount = MathHelper.sin((float) (flashTicks * 2 * Math.PI / FLASH_FREQ));
        return lerp(amount, glowing ? MIN_GLOWING : MIN, glowing ? MAX_GLOWING : MAX);
    }

    protected boolean noFlash() {
        return false;
    }

    private Color getOriginalColor(Entity entity) {
        return entity.isGlowing() || ((IEntityMixin) entity).callGetSharedFlag(6) ? SOUL_OUTLINE_COLOR_GLOWING : SOUL_OUTLINE_COLOR;
    }

    private static Color lerp(@Range(from = -1, to = 1) float amount, Color a, Color b) {
        amount = (amount + 1) / 2;
        return new Color((int) MathHelper.lerp(amount, a.getRed(), b.getRed()),
                (int) MathHelper.lerp(amount, a.getGreen(), b.getGreen()),
                (int) MathHelper.lerp(amount, a.getBlue(), b.getBlue()),
                (int) MathHelper.lerp(amount, a.getAlpha(), b.getAlpha()));
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.SOUL_CONTROL;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public static class NoFlash extends SoulControlHighlighter {
        public NoFlash(UUID entityUUID, long highlightTicksRemaining) {
            super(entityUUID, highlightTicksRemaining);
        }

        public NoFlash(UUID entityUUID, CompoundNBT compoundNBT) {
            super(entityUUID, compoundNBT);
        }

        @Override
        protected boolean noFlash() {
            return true;
        }

        @Override
        public HighlighterType getType() {
            return HighlighterType.NO_FLASH_SOUL_CONTROL;
        }
    }
}
