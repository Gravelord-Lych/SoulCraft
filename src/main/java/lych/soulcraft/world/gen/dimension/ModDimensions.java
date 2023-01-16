package lych.soulcraft.world.gen.dimension;

import lych.soulcraft.SoulCraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ModDimensions {
    public static final RegistryKey<World> ESV = RegistryKey.create(Registry.DIMENSION_REGISTRY, SoulCraft.prefix(ModDimensionNames.ESV));
    public static final RegistryKey<World> ETHEREAL = RegistryKey.create(Registry.DIMENSION_REGISTRY, SoulCraft.prefix(ModDimensionNames.ETHEREAL));
    public static final RegistryKey<World> SOUL_LAND = RegistryKey.create(Registry.DIMENSION_REGISTRY, SoulCraft.prefix(ModDimensionNames.SOUL_LAND));
    public static final RegistryKey<World> SUBWORLD = RegistryKey.create(Registry.DIMENSION_REGISTRY, SoulCraft.prefix(ModDimensionNames.SUBWORLD));

    private ModDimensions() {}
}
