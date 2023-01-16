package lych.soulcraft.entity.ai.brain.memory;

import lych.soulcraft.SoulCraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.Optional;

import static lych.soulcraft.SoulCraft.make;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModMemories {
    public static final MemoryModuleType<List<MobEntity>> MONSTERS = new MemoryModuleType<>(Optional.empty());
    public static final MemoryModuleType<List<MobEntity>> VISIBLE_MONSTERS = new MemoryModuleType<>(Optional.empty());
    public static final MemoryModuleType<MobEntity> NEAREST_MONSTER = new MemoryModuleType<>(Optional.empty());
    public static final MemoryModuleType<MobEntity> NEAREST_VISIBLE_MONSTER = new MemoryModuleType<>(Optional.empty());

    @SubscribeEvent
    public static void registerMemories(RegistryEvent.Register<MemoryModuleType<?>> event) {
        IForgeRegistry<MemoryModuleType<?>> registry = event.getRegistry();
        registry.register(make(MONSTERS, "monsters"));
        registry.register(make(VISIBLE_MONSTERS, "visible_monsters"));
        registry.register(make(NEAREST_MONSTER, "nearest_monster"));
        registry.register(make(NEAREST_VISIBLE_MONSTER, "nearest_visible_monster"));
    }
}
