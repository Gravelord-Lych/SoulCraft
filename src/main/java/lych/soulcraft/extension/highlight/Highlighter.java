package lych.soulcraft.extension.highlight;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.PriorityQueue;

public interface Highlighter extends Comparable<Highlighter> {
    @Nullable
    Color getColor(ServerWorld level, PriorityQueue<Highlighter> queue);

    long getHighlightTicks();

    void setHighlightTicks(long highlightTicks);

    CompoundNBT save();

    HighlighterType getType();

    default int getPriority() {
        return 1000;
    }

    @Override
    default int compareTo(Highlighter o) {
        return Integer.compare(getPriority(), o.getPriority());
    }

    default float @Nullable [] getMixColor(ServerWorld level, Entity entity) {
        return null;
    }
}
