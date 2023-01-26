package lych.soulcraft.world.gen.biome;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.world.gen.biome.provider.SoulLandBiomeProvider;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static lych.soulcraft.SoulCraft.make;
import static net.minecraftforge.common.BiomeDictionary.addTypes;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBiomes {
    public static final Type ESV_TYPE = Type.getType("ESV");
    public static final Type SOUL_TYPE = Type.getType("SOUL");
    public static final RegistryKey<Biome> ESV = makeKey(ModBiomeNames.ESV);
    public static final RegistryKey<Biome> INNERMOST_PLATEAU = makeKey(ModBiomeNames.INNERMOST_PLATEAU);
    public static final RegistryKey<Biome> INNERMOST_SOUL_LAND = makeKey(ModBiomeNames.INNERMOST_SOUL_LAND);
    public static final RegistryKey<Biome> PARCHED_DESERT = makeKey(ModBiomeNames.PARCHED_DESERT);
    public static final RegistryKey<Biome> PARCHED_DESERT_HILLS = makeKey(ModBiomeNames.PARCHED_DESERT_HILLS);
    public static final RegistryKey<Biome> SOUL_MOUNTAINS = makeKey(ModBiomeNames.SOUL_MOUNTAINS);
    public static final RegistryKey<Biome> SOUL_PLAINS = makeKey(ModBiomeNames.SOUL_PLAINS);
    public static final RegistryKey<Biome> SOUL_SAND_BEACH = makeKey(ModBiomeNames.SOUL_SAND_BEACH);
    public static final RegistryKey<Biome> SOUL_LAVA_OCEAN = makeKey(ModBiomeNames.SOUL_LAVA_OCEAN);
    public static final RegistryKey<Biome> UNSTABLE_SOUL_LAVA_OCEAN = makeKey(ModBiomeNames.UNSTABLE_SOUL_LAVA_OCEAN);
    public static final RegistryKey<Biome> WARPED_PLAINS = makeKey(ModBiomeNames.WARPED_PLAINS);
    public static final RegistryKey<Biome> WARPED_PLAINS_EDGE = makeKey(ModBiomeNames.WARPED_PLAINS_EDGE);
    public static final RegistryKey<Biome> WARPED_HILLS = makeKey(ModBiomeNames.WARPED_HILLS);

    @SubscribeEvent
    public static void registerBiomes(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        registry.register(make(ModBiomeMakers.makeESVBiome(), ModBiomeNames.ESV));
        registry.register(make(ModBiomeMakers.makeInnermostSoulLand(0.16f, 0.03f), ModBiomeNames.INNERMOST_SOUL_LAND));
        registry.register(make(ModBiomeMakers.makeInnermostSoulLand(1, 0.02f), ModBiomeNames.INNERMOST_PLATEAU));
        registry.register(make(ModBiomeMakers.makeParchedDesertBiome(0.16f, 0.06f), ModBiomeNames.PARCHED_DESERT));
        registry.register(make(ModBiomeMakers.makeParchedDesertBiome(0.45f, 0.3f), ModBiomeNames.PARCHED_DESERT_HILLS));
        registry.register(make(ModBiomeMakers.makeSoulBiome(1, 0.3f), ModBiomeNames.SOUL_MOUNTAINS));
        registry.register(make(ModBiomeMakers.makeSoulBiome(0.15f, 0.05f), ModBiomeNames.SOUL_PLAINS));
        registry.register(make(ModBiomeMakers.makeSoulBeach(0, 0.01f), ModBiomeNames.SOUL_SAND_BEACH));
        registry.register(make(ModBiomeMakers.makeSoulLavaOcean(-1, 0.075f), ModBiomeNames.SOUL_LAVA_OCEAN));
        registry.register(make(ModBiomeMakers.makeSoulLavaOcean(-0.6f, 0.32f), ModBiomeNames.UNSTABLE_SOUL_LAVA_OCEAN));
        registry.register(make(ModBiomeMakers.makeWarpedBiome(0.15f, 0.045f), ModBiomeNames.WARPED_PLAINS));
        registry.register(make(ModBiomeMakers.makeWarpedBiome(0.15f, 0.045f, true), ModBiomeNames.WARPED_PLAINS_EDGE));
        registry.register(make(ModBiomeMakers.makeWarpedBiome(0.45f, 0.3f), ModBiomeNames.WARPED_HILLS));
    }

    @SubscribeEvent
    public static void registerBiomeProviders(FMLCommonSetupEvent event) {
        Registry.register(Registry.BIOME_SOURCE, SoulLandBiomeProvider.SOUL_LAND, SoulLandBiomeProvider.CODEC);
    }

    private static RegistryKey<Biome> makeKey(String name) {
        return RegistryKey.create(Registry.BIOME_REGISTRY, SoulCraft.prefix(name));
    }

    static {
        addTypes(ESV, Type.COLD, Type.DRY, ESV_TYPE);
        addTypes(INNERMOST_PLATEAU, Type.PLATEAU, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE);
        addTypes(INNERMOST_SOUL_LAND, Type.PLAINS, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE);
        addTypes(PARCHED_DESERT, Type.SANDY, Type.HOT, Type.DRY);
        addTypes(SOUL_MOUNTAINS, Type.MOUNTAIN, Type.HOT, Type.DRY, SOUL_TYPE);
        addTypes(SOUL_PLAINS, Type.PLAINS, Type.HOT, Type.DRY, SOUL_TYPE);
        addTypes(SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, SOUL_TYPE);
        addTypes(UNSTABLE_SOUL_LAVA_OCEAN, Type.OCEAN, Type.HOT, Type.DRY, Type.RARE, SOUL_TYPE);
    }
}
