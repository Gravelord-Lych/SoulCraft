package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.BipedModels;
import lych.soulcraft.entity.monster.boss.esv.SoulControllerEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SoulControllerRenderer extends BipedRenderer<SoulControllerEntity, BipedModels.Size64<SoulControllerEntity>> {
    private static final ResourceLocation SOUL_CONTROLLER = SoulCraft.prefixTex("entity/esv/voidwalker_base.png");

    public SoulControllerRenderer(EntityRendererManager manager) {
        super(manager, new BipedModels.Size64<>(), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(SoulControllerEntity controller) {
        return SOUL_CONTROLLER;
    }
}
