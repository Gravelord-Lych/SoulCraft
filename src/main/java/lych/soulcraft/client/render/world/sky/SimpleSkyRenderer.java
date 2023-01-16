package lych.soulcraft.client.render.world.sky;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ISkyRenderHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class SimpleSkyRenderer implements ISkyRenderHandler {
    private final ResourceLocation skyLocation;
    private final Color color;

    public SimpleSkyRenderer(ResourceLocation skyLocation, Color color) {
        this.skyLocation = skyLocation;
        this.color = color;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void render(int ticks, float partialTicks, MatrixStack matrixStack, ClientWorld world, Minecraft mc) {
        renderSky(ticks, matrixStack, world, mc.getTextureManager());
    }

    @SuppressWarnings("deprecation")
    private void renderSky(int ticks, MatrixStack stack, ClientWorld world, TextureManager manager) {
        RenderSystem.disableAlphaTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        Color color = getColor(ticks, world);
        RenderSystem.color3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
        manager.bind(skyLocation);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        for (int i = 0; i < 6; ++i) {
            stack.pushPose();
            switch (i) {
                case 1:
                    stack.mulPose(Vector3f.XP.rotationDegrees(90));
                    break;
                case 2:
                    stack.mulPose(Vector3f.XP.rotationDegrees(-90));
                    break;
                case 3:
                    stack.mulPose(Vector3f.XP.rotationDegrees(180));
                    break;
                case 4:
                    stack.mulPose(Vector3f.ZP.rotationDegrees(90));
                    break;
                case 5:
                    stack.mulPose(Vector3f.ZP.rotationDegrees(-90));
                    break;
            }
            Matrix4f matrix4f = stack.last().pose();
            begin(builder);

            builder.vertex(matrix4f, -100, -100, -100).uv(0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            builder.vertex(matrix4f, -100, -100, 100).uv(0, 16).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            builder.vertex(matrix4f, 100, -100, 100).uv(16, 16).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            builder.vertex(matrix4f, 100, -100, -100).uv(16, 0).color(color.getRed(), color.getGreen(), color.getBlue(), 255).endVertex();
            tessellator.end();
            stack.popPose();
        }
        RenderSystem.depthMask(true);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
    }

    @SuppressWarnings("deprecation")
    protected void begin(BufferBuilder builder) {
        builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
    }

    protected Color getColor(int ticks, ClientWorld world) {
        return color;
    }

    public ResourceLocation getSkyLocation() {
        return skyLocation;
    }
}
