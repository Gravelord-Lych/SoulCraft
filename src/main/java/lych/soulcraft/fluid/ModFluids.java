package lych.soulcraft.fluid;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.block.ModBlocks;
import lych.soulcraft.item.ModItems;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

import static net.minecraftforge.fluids.ForgeFlowingFluid.Properties;

@Mod.EventBusSubscriber(modid = SoulCraft.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModFluids {
    private static final ResourceLocation SOUL_LAVA_STILL_TEXTURE = SoulCraft.prefix("block/" + ModFluidNames.still(ModFluidNames.SOUL_LAVA));
    private static final ResourceLocation FLOWING_SOUL_LAVA_TEXTURE = SoulCraft.prefix("block/" + ModFluidNames.flowing(ModFluidNames.SOUL_LAVA));
    private static final String SOUL_LAVA_TRANSLATION_KEY = SoulCraft.prefixMsg("block", ModFluidNames.SOUL_LAVA);

//    public static final Color water = new Color(0xff3f76e4);
    public static final Properties PROPERTIES = new Properties(() -> ModFluids.SOUL_LAVA, () -> ModFluids.FLOWING_SOUL_LAVA, FluidAttributes.builder(SOUL_LAVA_STILL_TEXTURE, FLOWING_SOUL_LAVA_TEXTURE).luminosity(15).density(3000).viscosity(6000).temperature(2300).translationKey(SOUL_LAVA_TRANSLATION_KEY).sound(SoundEvents.BUCKET_FILL_LAVA, SoundEvents.BUCKET_EMPTY_LAVA)).bucket(() -> ModItems.SOUL_LAVA_BUCKET).block(() -> ModBlocks.SOUL_LAVA_FLUID_BLOCK);
    public static final FlowingFluid SOUL_LAVA = new SoulLavaFluid.Source(PROPERTIES);
    public static final FlowingFluid FLOWING_SOUL_LAVA = new SoulLavaFluid.Flowing(PROPERTIES);

    private ModFluids() {}

    @SubscribeEvent
    public static void registerFluids(RegistryEvent.Register<Fluid> event) {
        IForgeRegistry<Fluid> registry = event.getRegistry();
        registry.register(SoulCraft.make(SOUL_LAVA, ModFluidNames.SOUL_LAVA));
        registry.register(SoulCraft.make(FLOWING_SOUL_LAVA, ModFluidNames.flowing(ModFluidNames.SOUL_LAVA)));
    }
}
