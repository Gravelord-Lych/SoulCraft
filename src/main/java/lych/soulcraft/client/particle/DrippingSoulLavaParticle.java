package lych.soulcraft.client.particle;

import com.google.common.base.Preconditions;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import lych.soulcraft.util.ModConstants;

@OnlyIn(Dist.CLIENT)
public class DrippingSoulLavaParticle extends DrippingParticle {
    private int redModifier = -1;
    private int greenModifier = -1;
    private int blueModifier = -1;

    public DrippingSoulLavaParticle(ClientWorld world, double x, double y, double z, Fluid fluid, IParticleData data) {
        super(world, x, y, z, fluid, data);
    }

    @Override
    protected void preMoveUpdate() {
        if (redModifier < 0) {
            int redModifier = 1;

            float current;
            float next;

            while (true) {
                current = redModifier / (redModifier * 2 + 40F);
                next = redModifier * 2 / (redModifier * 4 + 40F);
                if (Math.abs(current - ModConstants.SoulLava.SOUL_LAVA_R) <= Math.abs(next - ModConstants.SoulLava.SOUL_LAVA_R)) {
                    break;
                }
                redModifier *= 2;
            }

            this.redModifier = redModifier;
        }

        if (greenModifier < 0 || blueModifier < 0) {
            greenModifier = redModifier * Math.round(ModConstants.SoulLava.SOUL_LAVA_G / ModConstants.SoulLava.SOUL_LAVA_R) * 6;
            blueModifier = redModifier * Math.round(ModConstants.SoulLava.SOUL_LAVA_B / ModConstants.SoulLava.SOUL_LAVA_G) * 6;
        }

        Preconditions.checkState(redModifier > 0 && greenModifier > 0 && blueModifier > 0);

        final boolean bluer = ModConstants.SoulLava.SOUL_LAVA_B > ModConstants.SoulLava.SOUL_LAVA_G;

        rCol = redModifier / (float) (40 - lifetime + redModifier * 2);
        gCol = bluer ? greenModifier / (float) (40 - lifetime + greenModifier) : 1;
        bCol = bluer ? 1 : blueModifier / (float) (40 - lifetime + blueModifier);

        super.preMoveUpdate();
    }
}
