package lych.soulcraft.world.gen.biome.sll;

import lych.soulcraft.world.gen.biome.ModBiomes;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum SLDestabilizeLayer implements ICastleTransformer {
    INSTANCE;

    private static final int DESTABILIZE_PROB_INV = 75;

    @Override
    public int apply(INoiseRandom random, int n, int e, int s, int w, int self) {
        return SLLayer.allOcean(n, e, s, w, self) && random.nextRandom(DESTABILIZE_PROB_INV) == 0 ? SLLayer.getId(ModBiomes.UNSTABLE_SOUL_LAVA_OCEAN) : self;
    }
}
