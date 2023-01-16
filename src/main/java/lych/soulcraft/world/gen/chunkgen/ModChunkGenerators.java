package lych.soulcraft.world.gen.chunkgen;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.world.gen.dimension.ModDimensionNames;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static lych.soulcraft.SoulCraft.prefix;
import static net.minecraft.util.registry.Registry.register;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModChunkGenerators {
    private ModChunkGenerators() {}

    @SubscribeEvent
    public static void registerChunkGenerators(FMLCommonSetupEvent event) {
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.ESV), ESVChunkGenerator.CODEC);
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.ETHEREAL), EtherealChunkGenerator.CODEC);
        register(Registry.CHUNK_GENERATOR, prefix(ModDimensionNames.SUBWORLD), SubworldChunkGenerator.CODEC);
    }
}
