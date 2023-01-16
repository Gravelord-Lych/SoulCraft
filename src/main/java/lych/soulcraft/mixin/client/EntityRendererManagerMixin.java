package lych.soulcraft.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import lych.soulcraft.api.client.event.RenderSoulFireOverlayEvent;
import lych.soulcraft.client.SoulRenderers;
import lych.soulcraft.util.SoulFireHelper;
import lych.soulcraft.util.mixin.IEntityMixin;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityRendererManager.class)
public abstract class EntityRendererManagerMixin {
    @Shadow
    public ActiveRenderInfo camera;

    @Shadow protected abstract void renderFlame(MatrixStack p_229095_1_, IRenderTypeBuffer p_229095_2_, Entity p_229095_3_);

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRendererManager;renderFlame(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;Lnet/minecraft/entity/Entity;)V"))
    private void renderSoulFlame(EntityRendererManager instance, MatrixStack stack, IRenderTypeBuffer buffer, Entity entity) {
        if (/*!OptiFineHandler.isOptiFineLoaded() && */((IEntityMixin) entity).displaySoulFireAnimation()) {
            RenderSoulFireOverlayEvent soul = new RenderSoulFireOverlayEvent(entity, stack, RenderSoulFireOverlayEvent.Type.ENTITY, buffer, null, null);
            MinecraftForge.EVENT_BUS.post(soul);
            Event.Result result = soul.getResult();
            switch (result) {
                case ALLOW:
                    renderFlame(stack, buffer, entity);
                    break;
                case DEFAULT:
                    renderSoulFireOnEntity(stack, buffer, entity);
                    break;
            }
        } else {
            renderFlame(stack, buffer, entity);
        }
    }

    private void renderSoulFireOnEntity(MatrixStack stack, IRenderTypeBuffer buffer, Entity entity) {
        TextureAtlasSprite sprite = SoulFireHelper.SOUL_FIRE_0.sprite();
        TextureAtlasSprite sprite1 = SoulFireHelper.SOUL_FIRE_1.sprite();
        stack.pushPose();
        float f = entity.getBbWidth() * 1.4F;
        stack.scale(f, f, f);
        float f1 = 0.5F;
        float f2 = 0.0F;
        float f3 = entity.getBbHeight() / f;
        float f4 = 0.0F;
        stack.mulPose(Vector3f.YP.rotationDegrees(-camera.getYRot()));
        stack.translate(0.0D, 0.0D, -0.3F + (float)((int)f3) * 0.02F);
        float f5 = 0.0F;
        int i = 0;
        IVertexBuilder ivertexbuilder = buffer.getBuffer(Atlases.cutoutBlockSheet());

        for(MatrixStack.Entry matrixstack$entry = stack.last(); f3 > 0.0F; ++i) {
            TextureAtlasSprite sprite2 = i % 2 == 0 ? sprite : sprite1;
            float f6 = sprite2.getU0();
            float f7 = sprite2.getV0();
            float f8 = sprite2.getU1();
            float f9 = sprite2.getV1();
            if (i / 2 % 2 == 0) {
                float f10 = f8;
                f8 = f6;
                f6 = f10;
            }

            SoulRenderers.fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
            SoulRenderers.fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
            SoulRenderers.fireVertex(matrixstack$entry, ivertexbuilder, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
            SoulRenderers.fireVertex(matrixstack$entry, ivertexbuilder, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
            f3 -= 0.45F;
            f4 -= 0.45F;
            f1 *= 0.9F;
            f5 += 0.03F;
        }

        stack.popPose();
    }

}
