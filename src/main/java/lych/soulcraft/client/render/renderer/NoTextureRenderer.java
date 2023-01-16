package lych.soulcraft.client.render.renderer;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class NoTextureRenderer<T extends Entity> extends EntityRenderer<T> {
    public NoTextureRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @SuppressWarnings("deprecation")
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return AtlasTexture.LOCATION_BLOCKS;
    }
}
