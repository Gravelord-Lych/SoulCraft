package lych.soulcraft.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import lych.soulcraft.client.SoulRenderers;
import lych.soulcraft.util.mixin.IItemStackMixin;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.client.ForgeHooksClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow public abstract void renderModelLists(IBakedModel p_229114_1_, ItemStack p_229114_2_, int p_229114_3_, int p_229114_4_, MatrixStack p_229114_5_, IVertexBuilder p_229114_6_);

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderModelLists(Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/item/ItemStack;IILcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;)V"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void renderSoulFoil(ItemStack stack, ItemCameraTransforms.TransformType type, boolean leftHandHackery, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay, IBakedModel model, CallbackInfo ci, boolean flag, boolean flag1, RenderType rendertype) {
        if (((IItemStackMixin) (Object) stack).hasSoulFoil()) {
            IVertexBuilder newBuilder;
            if (stack.getItem() == Items.COMPASS) {
                matrix.pushPose();
                MatrixStack.Entry entry = matrix.last();

                if (type == ItemCameraTransforms.TransformType.GUI) {
                    entry.pose().multiply(0.5F);
                } else if (type.firstPerson()) {
                    entry.pose().multiply(0.75F);
                }

                if (flag1) {
                    newBuilder = SoulRenderers.getCompassSoulFoilBufferDirect(buffer, rendertype, entry);
                } else {
                    newBuilder = SoulRenderers.getCompassSoulFoilBuffer(buffer, rendertype, entry);
                }

                matrix.popPose();
            } else if (flag1) {
                newBuilder = SoulRenderers.getSoulFoilBufferDirect(buffer, rendertype, true);
            } else {
                newBuilder = SoulRenderers.getSoulFoilBuffer(buffer, rendertype, true);
            }
            renderModelLists(model, stack, combinedLight, combinedOverlay, matrix, newBuilder);
            matrix.popPose();
            ci.cancel();
        }
    }

//  Fail-safe, OptiFine seems to redirect this.
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/client/ForgeHooksClient;drawItemLayered(Lnet/minecraft/client/renderer/ItemRenderer;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/item/ItemStack;Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;IIZ)V", remap = false), require = 0)
    private void redirectToRenderSoulFoil(ItemRenderer renderer, IBakedModel modelIn, ItemStack itemStackIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn, boolean fabulous) {
        for (Pair<IBakedModel, RenderType> layerModel : modelIn.getLayerModels(itemStackIn, fabulous)) {
            IBakedModel layer = layerModel.getFirst();
            RenderType type = layerModel.getSecond();
            ForgeHooksClient.setRenderLayer(type);
            IVertexBuilder builder;
            if (((IItemStackMixin) (Object) itemStackIn).hasSoulFoil()) {
                if (fabulous) {
                    builder = SoulRenderers.getSoulFoilBufferDirect(bufferIn, type, true);
                } else {
                    builder = SoulRenderers.getSoulFoilBuffer(bufferIn, type, true);
                }
            } else {
                if (fabulous) {
                    builder = ItemRenderer.getFoilBufferDirect(bufferIn, type, true, itemStackIn.hasFoil());
                } else {
                    builder = ItemRenderer.getFoilBuffer(bufferIn, type, true, itemStackIn.hasFoil());
                }
            }
            renderer.renderModelLists(layer, itemStackIn, combinedLightIn, combinedOverlayIn, matrixStackIn, builder);
        }
        ForgeHooksClient.setRenderLayer(null);
    }
}
