package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.monster.SoulSkeletonEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulSkeletonRenderer extends BipedRenderer<SoulSkeletonEntity, SkeletonModel<SoulSkeletonEntity>> {
    private static final ResourceLocation PURIFIED_SOUL_SKELETON = SoulCraft.prefixTex("entity/purified_soul_skeleton.png");
    private static final ResourceLocation SOUL_SKELETON = SoulCraft.prefixTex("entity/soul_skeleton.png");

    public SoulSkeletonRenderer(EntityRendererManager manager) {
        super(manager, new SkeletonModel<>(), 0.5f);
        addLayer(new BipedArmorLayer<>(this, new SkeletonModel<>(0.5f, true), new SkeletonModel<>(0.5f, true)));
    }

    @Override
    public ResourceLocation getTextureLocation(SoulSkeletonEntity skeleton) {
        return skeleton.isPurified() ? PURIFIED_SOUL_SKELETON : SOUL_SKELETON;
    }
}
