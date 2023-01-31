package lych.soulcraft.client.render.renderer;

import lych.soulcraft.entity.monster.voidwalker.AbstractVoidLasererEntity;
import lych.soulcraft.entity.monster.voidwalker.EtheArmorerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class EtheArmorerRenderer extends AbstractVoidLasererRenderer<EtheArmorerEntity> {
    private static final ResourceLocation ETHE_ARMORER_CLOTHES = AbstractVoidLasererEntity.prefixTex("ethe_armorer_clothes.png");

    public EtheArmorerRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(EtheArmorerEntity voidwalker) {
        return ETHE_ARMORER_CLOTHES;
    }
}
