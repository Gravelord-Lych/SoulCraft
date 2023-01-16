package lych.soulcraft.client.particle;

import lych.soulcraft.fluid.ModFluids;
import lych.soulcraft.util.ModConstants;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;

public class LandingSoulLavaFactory extends net.minecraft.client.particle.DripParticle.LandingLavaFactory {
    public LandingSoulLavaFactory(IAnimatedSprite sprite) {
        super(sprite);
    }

    @Override
    public Particle createParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
        DripParticle particle = new LandingParticle(world, x, y, z, ModFluids.SOUL_LAVA);
        particle.setColor(ModConstants.SoulLava.SOUL_LAVA_R, ModConstants.SoulLava.SOUL_LAVA_G, ModConstants.SoulLava.SOUL_LAVA_B);
        particle.pickSprite(sprite);
        return particle;
    }
}
