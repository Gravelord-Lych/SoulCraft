package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.SkeletonKingModel;
import lych.soulcraft.entity.monster.boss.SkeletonKingEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonKingRenderer extends BipedRenderer<SkeletonKingEntity, SkeletonKingModel<SkeletonKingEntity>> {
    private static final ResourceLocation SKELETON_KING = SoulCraft.prefixTex("entity/skeleton_king.png");

    public SkeletonKingRenderer(EntityRendererManager manager) {
        super(manager, new SkeletonKingModel<>(), 0.5f);
        addLayer(new BipedArmorLayer<>(this, new SkeletonKingModel<>(0.5f, true), new SkeletonKingModel<>(1, true)));
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonKingEntity skeleton) {
        return SKELETON_KING;
    }
}
