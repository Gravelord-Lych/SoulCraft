package lych.soulcraft.extension.soulpower.control;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntArrayNBT;
import net.minecraft.nbt.NBTUtil;

import java.util.Objects;
import java.util.UUID;

public class ControlFlag {
    private final UUID uuid;

    public ControlFlag(String name) {
        this(UUID.fromString(name));
    }

    public ControlFlag(UUID uuid) {
        this.uuid = uuid;
    }

    public final IntArrayNBT save() {
        return NBTUtil.createUUID(uuid);
    }

    public static ControlFlag load(INBT nbt) {
        return new ControlFlag(NBTUtil.loadUUID(nbt));
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlFlag that = (ControlFlag) o;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
