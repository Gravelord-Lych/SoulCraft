package lych.soulcraft.world.gen.carver;

import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public final class ModConfiguredCarvers {
    public static final ConfiguredCarver<ProbabilityConfig> SOUL_CAVE = ModCarvers.SOUL_CAVE.configured(new ProbabilityConfig(0.2f));

    private ModConfiguredCarvers() {}
}