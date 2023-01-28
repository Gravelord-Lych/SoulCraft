package lych.soulcraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.blaze3d.vertex.MatrixApplyingVertexBuilder;
import com.mojang.blaze3d.vertex.VertexBuilderUtils;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.util.mixin.IItemStackMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.math.vector.Vector3d;
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

    @SuppressWarnings("resource")
    public static void renderBannerPatterns(MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, ModelRenderer renderer, RenderMaterial material, boolean isBanner, List<Pair<BannerPattern, DyeColor>> list, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
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

    public static void translate(MatrixStack matrixStack) {
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrixStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
    }
}
