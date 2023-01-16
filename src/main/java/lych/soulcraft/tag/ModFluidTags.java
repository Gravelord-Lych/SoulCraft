package lych.soulcraft.tag;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.TagNames;
import net.minecraft.fluid.Fluid;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.common.Tags;

public final class ModFluidTags {
    public static final Tags.IOptionalNamedTag<Fluid> SOUL_LAVA = tag(TagNames.SOUL_LAVA.getPath());

    private ModFluidTags() {}

    private static Tags.IOptionalNamedTag<Fluid> tag(String name) {
        return FluidTags.createOptional(SoulCraft.prefix(name));
    }
}
