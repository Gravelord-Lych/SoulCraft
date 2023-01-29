package lych.soulcraft.client.render.renderer;

import lych.soulcraft.config.ConfigHelper;
import lych.soulcraft.entity.monster.voidwalker.AbstractVoidLasererEntity;
import lych.soulcraft.entity.monster.voidwalker.EtheArmorerEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class EtheArmorerRenderer extends AbstractVoidLasererRenderer<EtheArmorerEntity> {
    private static final ResourceLocation ETHE_ARMORER = AbstractVoidLasererEntity.prefixTex("ethe_armorer.png");

    public EtheArmorerRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(EtheArmorerEntity voidwalker) {
        return ETHE_ARMORER;
    }

    @Override
    protected RenderType makeRenderType(EtheArmorerEntity armorer, Entity target) {
        if (armorer.getAttackType() == null) {
            throwErrorIfFailhard();
            return RenderType.cutout();
        }
        return RenderType.entitySmoothCutout(armorer.getAttackType().getTextureLocation());
    }

    @Override
    protected int getSrcColor(EtheArmorerEntity armorer, Entity target) {
        if (armorer.getAttackType() == null) {
            throwErrorIfFailhard();
            return 0;
        }
        return armorer.getAttackType().getSrcColor();
    }

    @Override
    protected int getDestColor(EtheArmorerEntity armorer, Entity target) {
        if (armorer.getAttackType() == null) {
            throwErrorIfFailhard();
            return 0;
        }
        return armorer.getAttackType().getDestColor();
    }

    private static void throwErrorIfFailhard() {
        if (ConfigHelper.shouldFailhard()) {
            throw new AssertionError(ConfigHelper.FAILHARD_MESSAGE + "Attack type is null. This should never happen");
        }
    }

    @Override
    protected float getSrcScale(EtheArmorerEntity armorer, Entity target) {
        return 0.2f;
    }

    @Override
    protected float getDestScale(EtheArmorerEntity armorer, Entity target) {
        return 0.2f;
    }
}
