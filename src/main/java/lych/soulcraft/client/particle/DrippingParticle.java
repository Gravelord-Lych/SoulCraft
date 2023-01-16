package lych.soulcraft.client.particle;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrippingParticle extends DripParticle {
    private final IParticleData fallingParticle;

    public DrippingParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData data) {
        super(world, x, y, z, fluid);
        this.fallingParticle = data;
        this.gravity *= 0.02F;
        this.lifetime = 40;
    }

    @Override
    protected void preMoveUpdate() {
        if (lifetime-- <= 0) {
            remove();
            level.addParticle(fallingParticle, x, y, z, xd, yd, zd);
        }
    }

    @Override
    protected void postMoveUpdate() {
        xd *= 0.02;
        yd *= 0.02;
        zd *= 0.02;
    }
}
