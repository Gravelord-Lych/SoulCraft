package lych.soulcraft.data;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.tag.ModFluidTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeFluidTagsProvider;
import lych.soulcraft.fluid.ModFluids;

public class FluidTagDataGen extends ForgeFluidTagsProvider {
    public FluidTagDataGen(DataGenerator gen, ExistingFileHelper existingFileHelper) {
        super(gen, existingFileHelper);
    }

    @Override
    public void addTags() {
        tag(FluidTags.LAVA).add(ModFluids.SOUL_LAVA, ModFluids.FLOWING_SOUL_LAVA);
        tag(ModFluidTags.SOUL_LAVA).add(ModFluids.SOUL_LAVA, ModFluids.FLOWING_SOUL_LAVA);
    }

    @Override
    public String getName() {
        return "Fluid Tags: " + SoulCraft.MOD_ID;
    }
}
