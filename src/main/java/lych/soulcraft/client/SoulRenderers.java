package lych.soulcraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.extension.fire.Fire;
import lych.soulcraft.util.FireBlockHelper;
import lych.soulcraft.util.mixin.IItemStackMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class SoulRenderers {
    public static IVertexBuilder getCompassSoulFoilBufferDirect(IRenderTypeBuffer buffer, RenderType type, MatrixStack.Entry entry) {
        return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(buffer.getBuffer(ModRenderTypes.SOUL_GLINT_DIRECT), entry.pose(), entry.normal()), buffer.getBuffer(type));
    }

    public static IVertexBuilder getCompassSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, MatrixStack.Entry entry) {
        return VertexBuilderUtils.create(new MatrixApplyingVertexBuilder(buffer.getBuffer(ModRenderTypes.SOUL_GLINT), entry.pose(), entry.normal()), buffer.getBuffer(type));
    }

    public static IVertexBuilder getSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, boolean useGlint) {
        if (Minecraft.useShaderTransparency() && type == Atlases.translucentItemSheet()) {
            return VertexBuilderUtils.create(buffer.getBuffer(ModRenderTypes.SOUL_GLINT_TRANSLUCENT), buffer.getBuffer(type));
        } else {
            return VertexBuilderUtils.create(buffer.getBuffer(useGlint ? ModRenderTypes.SOUL_GLINT : ModRenderTypes.ENTITY_SOUL_GLINT), buffer.getBuffer(type));
        }
    }

    public static IVertexBuilder getSoulFoilBufferDirect(IRenderTypeBuffer buffer, RenderType type, boolean useGlintDirect) {
        return VertexBuilderUtils.create(buffer.getBuffer(useGlintDirect ? ModRenderTypes.SOUL_GLINT_DIRECT : ModRenderTypes.ENTITY_SOUL_GLINT_DIRECT), buffer.getBuffer(type));
    }

    public static IVertexBuilder getArmorSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type) {
        return getArmorSoulFoilBuffer(buffer, type, false);
    }

    public static IVertexBuilder getArmorSoulFoilBuffer(IRenderTypeBuffer buffer, RenderType type, boolean useArmorGlint) {
        return VertexBuilderUtils.create(buffer.getBuffer(useArmorGlint ? ModRenderTypes.ARMOR_SOUL_GLINT : ModRenderTypes.ARMOR_ENTITY_SOUL_GLINT), buffer.getBuffer(type));
    }

    public static void fireVertex(MatrixStack.Entry p_229090_0_, IVertexBuilder p_229090_1_, float p_229090_2_, float p_229090_3_, float p_229090_4_, float p_229090_5_, float p_229090_6_) {
        p_229090_1_.vertex(p_229090_0_.pose(), p_229090_2_, p_229090_3_, p_229090_4_).color(255, 255, 255, 255).uv(p_229090_5_, p_229090_6_).overlayCoords(0, 10).uv2(240).normal(p_229090_0_.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    public static void renderBannerPatterns(MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, ModelRenderer renderer, RenderMaterial material, boolean isBanner, List<Pair<BannerPattern, DyeColor>> list, ItemStack stack) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            renderer.render(matrix, material.sprite().wrap(getSoulFoilBufferDirect(buffer, material.renderType(RenderType::entitySolid), true)), combinedLight, combinedOverlay);
        } else {
            renderer.render(matrix, material.buffer(buffer, RenderType::entitySolid, stack.hasFoil()), combinedLight, combinedOverlay);
        }
        for (int i = 0; i < 17 && i < list.size(); ++i) {
            Pair<BannerPattern, DyeColor> pair = list.get(i);
            float[] diffuseColors = pair.getSecond().getTextureDiffuseColors();
            RenderMaterial newMaterial = new RenderMaterial(isBanner ? Atlases.BANNER_SHEET : Atlases.SHIELD_SHEET, pair.getFirst().location(isBanner));
            renderer.render(matrix, newMaterial.buffer(buffer, RenderType::entityNoOutline), combinedLight, combinedOverlay, diffuseColors[0], diffuseColors[1], diffuseColors[2], 1.0F);
        }
    }

    public static void renderSoulFireOnPlayer(Minecraft minecraft, Fire fire, MatrixStack stack) {
        BufferBuilder builder = Tessellator.getInstance().getBuilder();
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableTexture();
        TextureAtlasSprite sprite = FireBlockHelper.SOUL_FIRE_1.sprite();
        minecraft.getTextureManager().bind(sprite.atlas().location());
        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float averageU = (u0 + u1) / 2.0F;
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();
        float averageV = (v0 + v1) / 2.0F;
        float ratio = sprite.uvShrinkRatio();
        float ub = MathHelper.lerp(ratio, u0, averageU);
        float ua = MathHelper.lerp(ratio, u1, averageU);
        float vb = MathHelper.lerp(ratio, v0, averageV);
        float va = MathHelper.lerp(ratio, v1, averageV);

        for(int i = 0; i < 2; ++i) {
            stack.pushPose();
            stack.translate((float)(-(i * 2 - 1)) * 0.24F, (double)-0.3F, 0.0D);
            stack.mulPose(Vector3f.YP.rotationDegrees((float)(i * 2 - 1) * 10.0F));
            Matrix4f matrix4f = stack.last().pose();
            builder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            builder.vertex(matrix4f, -0.5F, -0.5F, -0.5F).color(1, 1, 1, 0.9F).uv(ua, va).endVertex();
            builder.vertex(matrix4f, 0.5F, -0.5F, -0.5F).color(1, 1, 1, 0.9F).uv(ub, va).endVertex();
            builder.vertex(matrix4f, 0.5F, 0.5F, -0.5F).color(1, 1, 1, 0.9F).uv(ub, vb).endVertex();
            builder.vertex(matrix4f, -0.5F, 0.5F, -0.5F).color(1, 1, 1, 0.9F).uv(ua, vb).endVertex();
            builder.end();
            WorldVertexBufferUploader.end(builder);
            stack.popPose();
        }
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }

    public static void translate(MatrixStack matrixStack) {
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
    }
}
