package lych.soulcraft.util.mixin;

import lych.soulcraft.client.LaserRenderingManager;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import org.jetbrains.annotations.Nullable;

public interface IWorldRendererMixin {
    @Nullable
    VertexBuffer getSkyBuffer();

    LaserRenderingManager getLaserRenderingManager();
}
