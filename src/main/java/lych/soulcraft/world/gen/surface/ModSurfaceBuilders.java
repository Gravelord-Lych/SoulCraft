package lych.soulcraft.world.gen.surface;

import lych.soulcraft.SoulCraft;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSurfaceBuilders {
    public static void registerSurfaceBuilders(RegistryEvent.Register<SurfaceBuilder<?>> event) {
        IForgeRegistry<SurfaceBuilder<?>> registry = event.getRegistry();
    }

    private static String suffix(String name) {
        return name + "_surface_builder";
    }
}
