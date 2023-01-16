package lych.soulcraft.util;

import com.google.common.base.MoreObjects;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class UUIDPointer implements Iterable<UUID>, Iterator<UUID> {
    private final UUID uuid;
    @Nullable
    private final UUIDPointer pointedUUID;

    public UUIDPointer(UUID uuid, @Nullable UUIDPointer pointedUUID) {
        this.uuid = uuid;
        this.pointedUUID = pointedUUID;
    }

    public UUID get() {
        return uuid;
    }

    public List<UUID> getPointedUUIDs() {
        return getPointedUUIDs(new ArrayList<>());
    }

    private List<UUID> getPointedUUIDs(List<UUID> pointers) {
        pointers.add(get());
        if (getPointed() == null) {
            return pointers;
        }
        return getPointed().getPointedUUIDs(pointers);
    }

    public UUID getPointedUUID() {
        if (getPointed() == null) {
            return get();
        }
        return getPointed().getPointedUUID();
    }

    @Nullable
    public UUIDPointer getPointed() {
        return pointedUUID;
    }

    public ListNBT save() {
        ListNBT listNBT = new ListNBT();
        getPointedUUIDs().stream().map(NBTUtil::createUUID).forEach(listNBT::add);
        return listNBT;
    }

    @Nullable
    public static UUIDPointer load(ListNBT listNBT) {
        List<UUID> list = listNBT.stream().map(NBTUtil::loadUUID).collect(Collectors.toList());
        if (list.isEmpty()) {
            return null;
        }
        return loadFrom(list);
    }

    public static UUIDPointer loadFrom(List<UUID> list) {
        return loadFrom(list, null);
    }

    public static UUIDPointer loadFrom(List<UUID> list, @Nullable UUIDPointer defaultValue) {
        if (list.isEmpty()) {
            throw new IllegalArgumentException("Invalid list: " + list);
        }
        UUIDPointer pointerIn = new UUIDPointer(list.get(0), defaultValue);
        if (list.size() == 1) {
            return pointerIn;
        } else {
            list.remove(0);
            return loadFrom(list, pointerIn);
        }
    }

    @Override
    public Iterator<UUID> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return getPointed() != null;
    }

    @Override
    public UUID next() {
        if (getPointed() == null) {
            throw new NoSuchElementException();
        }
        return getPointed().get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UUIDPointer uuids = (UUIDPointer) o;
        return Objects.equals(uuid, uuids.uuid) && Objects.equals(pointedUUID, uuids.pointedUUID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, getPointedUUID());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uuid", uuid)
                .add("pointedUUID", pointedUUID)
                .toString();
    }
}
