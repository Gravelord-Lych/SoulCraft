package lych.soulcraft.extension.key;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class InvokableManager {
    private static final Map<InvokableData, IInvokable> INVOKABLES = new HashMap<>();

    public static void register(FMLClientSetupEvent event, InvokableData data, IInvokable invokable) {
        Objects.requireNonNull(invokable);
        INVOKABLES.put(data, invokable);
        event.enqueueWork(() -> ClientRegistry.registerKeyBinding(data.getKey()));
    }

    public static ImmutableMap<InvokableData, IInvokable> getKeyInvokables() {
        return ImmutableMap.copyOf(INVOKABLES);
    }

    @SuppressWarnings("ConstantConditions")
    public static IInvokable get(UUID uuid) {
        return get(new InvokableData(uuid, null));
    }

    public static IInvokable get(InvokableData data) {
        return getKeyInvokables().get(data);
    }

//  Server logics
    private static final Object2IntMap<PlayerEntity> RECENTLY_PRESSED_MAP = new Object2IntOpenHashMap<>();
    private static final Object2IntMap<PlayerEntity> RECENTLY_PRESSED_TIMESTAMPS = new Object2IntOpenHashMap<>();

    public static Object2IntMap<PlayerEntity> getRecentlyPressedMap() {
        return Object2IntMaps.unmodifiable(RECENTLY_PRESSED_MAP);
    }

    public static void setRecentlyPressed(PlayerEntity player, int recentlyPressed) {
        RECENTLY_PRESSED_MAP.put(player, recentlyPressed);
    }

    public static Object2IntMap<PlayerEntity> getRecentlyPressedTimestamps() {
        return RECENTLY_PRESSED_TIMESTAMPS;
    }

    public static void setRecentlyPressedTimestamp(PlayerEntity player, int recentlyPressedTimestamp) {
        RECENTLY_PRESSED_TIMESTAMPS.put(player, recentlyPressedTimestamp);
    }
}
