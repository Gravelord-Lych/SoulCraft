package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.projectile.SoulArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulArrowRenderer extends ArrowRenderer<SoulArrowEntity> {
    private static final ResourceLocation SOUL_ARROW = SoulCraft.prefixTex("entity/projectiles/soul_arrow.png");

    public SoulArrowRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulArrowEntity arrow) {
        return SOUL_ARROW;
    }
}
