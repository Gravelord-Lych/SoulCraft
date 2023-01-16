package lych.soulcraft.world.gen.biome;

import lych.soulcraft.SoulCraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soulcraft.SoulCraft.make;
import static net.minecraftforge.common.BiomeDictionary.addTypes;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBiomes {
    public static final Type ESV_TYPE = Type.getType("ESV");
    public static final Type SOUL_TYPE = Type.getType("SOUL");
    public static final RegistryKey<Biome> ESV = makeKey(ModBiomeNames.ESV);
    public static final RegistryKey<Biome> SOUL_MOUNTAINS = makeKey(ModBiomeNames.SOUL_MOUNTAINS);
    public static final RegistryKey<Biome> SOUL_MOUNTAIN_EDGE_1 = makeKey(ModBiomeNames.SOUL_MOUNTAIN_EDGE_1);
    public static final RegistryKey<Biome> SOUL_MOUNTAIN_EDGE_2 = makeKey(ModBiomeNames.SOUL_MOUNTAIN_EDGE_2);
    public static final RegistryKey<Biome> SOUL_PLAINS = makeKey(ModBiomeNames.SOUL_PLAINS);

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        registry.register(make(ModBiomeMakers.makeESVBiome(), ModBiomeNames.ESV));
        registry.register(make(ModBiomeMakers.makeSoulBiome(2f, 0.4f), ModBiomeNames.SOUL_MOUNTAINS));
        registry.register(make(ModBiomeMakers.makeSoulBiome(1.2f, 0.3f), ModBiomeNames.SOUL_MOUNTAIN_EDGE_1));
        registry.register(make(ModBiomeMakers.makeSoulBiome(0.5f, 0.16f), ModBiomeNames.SOUL_MOUNTAIN_EDGE_2));
        registry.register(make(ModBiomeMakers.makeSoulBiome(0.1f, 0.08f), ModBiomeNames.SOUL_PLAINS));
    }

    private static RegistryKey<Biome> makeKey(String name) {
        return RegistryKey.create(Registry.BIOME_REGISTRY, SoulCraft.prefix(name));
    }

    static {
        addTypes(ESV, Type.COLD, ESV_TYPE);
        addTypes(SOUL_MOUNTAINS, Type.MOUNTAIN, Type.HOT, SOUL_TYPE);
        addTypes(SOUL_MOUNTAIN_EDGE_1, Type.MOUNTAIN, Type.HOT, SOUL_TYPE);
        addTypes(SOUL_MOUNTAIN_EDGE_2, Type.PLAINS, Type.HOT, SOUL_TYPE);
        addTypes(SOUL_PLAINS, Type.PLAINS, Type.HOT, SOUL_TYPE);
    }
}
