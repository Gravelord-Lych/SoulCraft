package lych.soulcraft.extension.highlight;

import lych.soulcraft.world.event.manager.NotLoadedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HighlighterType {
    public static final HighlighterType NO_HIGHLIGHT = new HighlighterType(Util.NIL_UUID, DummyHighlighter::new, DummyHighlighter::new);
    public static final HighlighterType MONSTER_VIEW = new HighlighterType("C9A92E28-1765-40AB-853A-F3874408A039", MonsterViewHighlighter::new, MonsterViewHighlighter::new);
    public static final HighlighterType NO_FLASH_SOUL_CONTROL = new HighlighterType("93F0A568-26FF-4674-95DE-59FCDD4B010D", SoulControlHighlighter.NoFlash::new, SoulControlHighlighter.NoFlash::new);
    public static final HighlighterType SOUL_CONTROL = new HighlighterType("9855C4A0-2B69-C250-92D2-A9230193F2BC", SoulControlHighlighter::new, SoulControlHighlighter::new);

    static {
        HIGHLIGHTERS = new HashMap<>();
        registerHighlighter(NO_HIGHLIGHT);
        registerHighlighter(MONSTER_VIEW);
        registerHighlighter(NO_FLASH_SOUL_CONTROL);
        registerHighlighter(SOUL_CONTROL);
    }

    private static final Map<UUID, HighlighterType> HIGHLIGHTERS;
    private final UUID uuid;
    private final HighlighterCreator creator;
    private final HighlighterLoader loader;

    public HighlighterType(String uuid, HighlighterCreator creator, HighlighterLoader loader) {
        this(UUID.fromString(uuid), creator, loader);
    }

    public HighlighterType(UUID uuid, HighlighterCreator creator, HighlighterLoader loader) {
        this.uuid = uuid;
        this.creator = creator;
        this.loader = loader;
    }

    public IHighlighter create(UUID entity, long highlightTicksRemaining) {
        return creator.create(entity, highlightTicksRemaining);
    }

    public IHighlighter load(UUID entityUUID, CompoundNBT compoundNBT) throws NotLoadedException {
        return loader.load(entityUUID, compoundNBT);
    }

    public UUID getUUID() {
        return uuid;
    }

    public static void registerHighlighter(HighlighterType type) {
        HIGHLIGHTERS.put(type.getUUID(), type);
    }

    public static HighlighterType get(UUID uuid) {
        return HIGHLIGHTERS.get(uuid);
    }

    @FunctionalInterface
    public interface HighlighterCreator {
        IHighlighter create(UUID entity, long highlightTicksRemaining);
    }

    @FunctionalInterface
    public interface HighlighterLoader {
        IHighlighter load(UUID entityUUID, CompoundNBT nbt) throws NotLoadedException;
    }
}
