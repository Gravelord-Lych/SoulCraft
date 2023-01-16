package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.RedstoneTurretModel;
import lych.soulcraft.entity.monster.raider.RedstoneMortarEntity;
import lych.soulcraft.entity.monster.raider.RedstoneTurretEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;

public class RedstoneTurretRenderer<T extends MobEntity> extends MobRenderer<T, RedstoneTurretModel<T>> {
    private final ResourceLocation textureLocation;

    public RedstoneTurretRenderer(EntityRendererManager manager, ResourceLocation textureLocation) {
        super(manager, new RedstoneTurretModel<>(), 0.5f);
        this.textureLocation = textureLocation;
    }

    @Override
    public ResourceLocation getTextureLocation(T turret) {
        return textureLocation;
    }

    @Override
    protected boolean isShaking(T turret) {
        return turret.deathTime > 0;
    }

    public static class Common extends RedstoneTurretRenderer<RedstoneTurretEntity> {
        private static final ResourceLocation REDSTONE_TURRET = SoulCraft.prefixTex("entity/raider/redstone_turret.png");

        public Common(EntityRendererManager manager) {
            super(manager, REDSTONE_TURRET);
        }
    }

    public static class Mortar extends RedstoneTurretRenderer<RedstoneMortarEntity> {
        private static final ResourceLocation REDSTONE_MORTAR = SoulCraft.prefixTex("entity/raider/redstone_mortar.png");

        public Mortar(EntityRendererManager manager) {
            super(manager, REDSTONE_MORTAR);
        }
    }
}
