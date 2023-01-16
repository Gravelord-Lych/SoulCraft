package lych.soulcraft.extension.highlight;

import com.google.common.base.MoreObjects;
import lych.soulcraft.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public abstract class AbstractHighlighter implements IHighlighter {
    protected final UUID entityUUID;
    private long highlightTicksRemaining;

    public AbstractHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        this.entityUUID = entityUUID;
        this.highlightTicksRemaining = highlightTicksRemaining;
    }

    public AbstractHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        this.entityUUID = entityUUID;
        this.highlightTicksRemaining = compoundNBT.getLong("HighlightTicksRemaining");
    }

    @Nullable
    @Override
    public Color getColor(ServerWorld level) {
        if (highlightTicksRemaining >= 0) {
            highlightTicksRemaining--;
        }
        Entity entity = level.getEntity(entityUUID);
        if (entity == null) {
            return Color.BLACK;
        }
        if (highlightTicksRemaining < 0 || !EntityUtils.isAlive(entity)) {
            return null;
        }
        return getColor(level, entity);
    }

    @Override
    public long getHighlightTicks() {
        return highlightTicksRemaining;
    }

    @Override
    public void setHighlightTicks(long highlightTicks) {
        this.highlightTicksRemaining = highlightTicks;
    }

    protected abstract Color getColor(ServerWorld level, Entity entity);

    protected void addAdditionalSaveData(CompoundNBT compoundNBT) {}

    @Override
    public CompoundNBT save() {
        CompoundNBT compoundNBT = new CompoundNBT();
        compoundNBT.putLong("HighlightTicksRemaining", highlightTicksRemaining);
        addAdditionalSaveData(compoundNBT);
        return compoundNBT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractHighlighter)) return false;
        AbstractHighlighter that = (AbstractHighlighter) o;
        return highlightTicksRemaining == that.highlightTicksRemaining && Objects.equals(entityUUID, that.entityUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityUUID, highlightTicksRemaining);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("entityUUID", entityUUID)
                .add("highlightTicksRemaining", highlightTicksRemaining);
    }
}
