package lych.soulcraft.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import it.unimi.dsi.fastutil.longs.Long2IntArrayMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.network.LaserNetwork.LaserRenderData;
import lych.soulcraft.util.mixin.ICustomLaserUser;
import lych.soulcraft.util.mixin.IWorldRendererMixin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

// FIXME - Lasers are not rendered when Fabulous! fanciness is used
@OnlyIn(Dist.CLIENT)
public class LaserRenderingManager {
    private final Supplier<ClientWorld> level;
    private final Queue<LaserRenderData> lasersToRender = new ArrayDeque<>();
    private final Long2IntMap laserTicksMap = new Long2IntArrayMap();
    private final IntSupplier ticks;

    public LaserRenderingManager(Supplier<ClientWorld> level, IntSupplier ticks) {
        this.level = level;
        this.ticks = ticks;
    }

    public static LaserRenderingManager getInstance() {
        return ((IWorldRendererMixin) Minecraft.getInstance().levelRenderer).getLaserRenderingManager();
    }

    public void addLaser(LaserRenderData data) {
        World level = this.level.get();
        if (level == null) {
            SoulCraft.LOGGER.error("Missing level for laser rendering.");
            return;
        }
        Entity entity = level.getEntity(data.getOwner());
        if (entity != null) {
            lasersToRender.offer(data);
            laserTicksMap.put(data.getId(), ticks.getAsInt() + data.getRenderTickCount());
        }
    }

    public void render(MatrixStack stack) {
        Iterator<LaserRenderData> iterator = lasersToRender.iterator();
        while (iterator.hasNext()) {
            LaserRenderData data = iterator.next();
            Entity entity = level.get().getEntity(data.getOwner());
            if (entity == null || ticks.getAsInt() > laserTicksMap.get(data.getId())) {
                iterator.remove();
                laserTicksMap.remove(data.getId());
            } else {
                doRender(data, stack, getRenderType(entity), data.getColor());
            }
        }
    }

    private static RenderType getRenderType(Entity entity) {
        return entity instanceof ICustomLaserUser ? ((ICustomLaserUser) entity).getLaserRenderType() : ModRenderTypes.DEFAULT_LASER;
    }

    private float getLifeOf(LaserRenderData data) {
        return (laserTicksMap.get(data.getId()) - ticks.getAsInt()) / ((float) data.getRenderTickCount());
    }

    private void doRender(LaserRenderData data, MatrixStack matrixStack, RenderType type, Color laserColor) {
        IRenderTypeBuffer.Impl buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        IVertexBuilder builder = buffer.getBuffer(type);

        Vector3d src = data.getSrc();
        double px = src.x;
        double py = src.y;
        double pz = src.z;
        Vector3d dest = data.getDest();
        double dx = dest.x;
        double dy = dest.y;
        double dz = dest.z;

        double tx = dx - px;
        double ty = dy - py;
        double tz = dz - pz;

        matrixStack.pushPose();

        SoulRenderers.translate(matrixStack);

        Matrix4f positionMatrix = matrixStack.last().pose();
        drawLaser(builder,
                positionMatrix,
                new Vector3f(src),
                laserColor.getRed() / 255f,
                laserColor.getGreen() / 255f,
                laserColor.getBlue() / 255f,
                getLifeOf(data),
                (float) tx,
                (float) ty,
                (float) tz);
        matrixStack.popPose();

        RenderSystem.disableDepthTest();
        buffer.endBatch(type);
    }

    private static void drawLaser(IVertexBuilder builder, Matrix4f positionMatrix, Vector3f pos, float r, float g, float b, float alpha, float tx, float ty, float tz) {
        builder.vertex(positionMatrix, pos.x(), pos.y(), pos.z())
                .color(r, g, b, alpha)
                .endVertex();
        builder.vertex(positionMatrix, pos.x() + tx, pos.y() + ty, pos.z() + tz)
                .color(r, g, b, alpha)
                .endVertex();
    }
}
