package lych.soulcraft.world.gen.carver.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.carver.ICarverConfig;

@Deprecated
public class SoulCaveConfig implements ICarverConfig {
    public static final Codec<SoulCaveConfig> CODEC = RecordCodecBuilder
            .create(instance -> instance.group(Codec
                    .intRange(0, 20)
                    .fieldOf("range")
                    .forGetter(SoulCaveConfig::getRange), Codec
                    .floatRange(0, 1)
                    .fieldOf("probability")
                    .forGetter(SoulCaveConfig::getProbability), Codec
                    .doubleRange(0, 10)
                    .fieldOf("scale")
                    .forGetter(SoulCaveConfig::getYScale))
                    .apply(instance, SoulCaveConfig::new));

    private final int range;
    private final float probability;
    private final double yScale;

    public SoulCaveConfig(int range, float probability, double yScale) {
        this.range = range;
        this.probability = probability;
        this.yScale = yScale;
    }

    public float getProbability() {
        return probability;
    }

    public int getRange() {
        return range;
    }

    public double getYScale() {
        return yScale;
    }
}
