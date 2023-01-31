package lych.soulcraft.extension.highlight;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public interface Highlighter extends Comparable<Highlighter> {
    @Nullable
    Color getColor(ServerWorld level);

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
}
