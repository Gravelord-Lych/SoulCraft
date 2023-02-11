package lych.soulcraft.extension.highlight;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.extension.control.Controller;
import lych.soulcraft.extension.control.SoulManager;
import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;

public class SoulControlHighlighter extends AbstractHighlighter {
    public static final float[] DEFAULT_COLOR = new float[]{0.5f, 0.8f, 1};
    private static final float[] GLOWING_COLOR = new float[]{Float.NaN, 0, 1};
    private static final int IS_GLOWING = 6;

    public SoulControlHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public SoulControlHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        if (!(entity instanceof MobEntity)) {
            return asColor(DEFAULT_COLOR);
        }
        PriorityQueue<Controller<?>> queue = SoulManager.get(level).getControllerData((MobEntity) entity).getSecond();
        if (queue.isEmpty()) {
            return asColor(DEFAULT_COLOR);
        }
        Controller<?> activeController = queue.element();
        ImmutableList.Builder<float[]> builder = ImmutableList.builder();
        builder.add(activeController.getColor());
        if (entity.isGlowing() || ((IEntityMixin) entity).callGetSharedFlag(IS_GLOWING)) {
            builder.add(GLOWING_COLOR);
        }
        return reduce(builder.build());
    }

    private static Color reduce(List<float[]> colors) {
        float[] hsb = colors.stream().reduce((a1, a2) -> new float[]{getHue(a1[0], a2[0]), (a1[1] + a2[1]) / 2, (a1[2] + a2[2]) / 2}).orElseThrow(() -> new IllegalArgumentException("Colors cannot be empty"));
        return asColor(hsb);
    }

    private static float getHue(float h1, float h2) {
        if (Float.isNaN(h1) && Float.isNaN(h2)) {
            return 0;
        }
        if (Float.isNaN(h1)) {
            return h2;
        }
        if (Float.isNaN(h2)) {
            return h1;
        }
        return (h1 + h2) / 2;
    }

    @NotNull
    private static Color asColor(float[] hsb) {
        return new Color(Color.HSBtoRGB(Float.isFinite(hsb[0]) ? hsb[0] : 0, hsb[1], hsb[2]));
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.SOUL_CONTROL;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
