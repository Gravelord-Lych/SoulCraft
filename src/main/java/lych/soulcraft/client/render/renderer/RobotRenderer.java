package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.BipedModels;
import lych.soulcraft.client.render.renderer.layer.Meta08ShieldLayer;
import lych.soulcraft.entity.monster.RobotEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RobotRenderer extends BipedRenderer<RobotEntity, BipedModels.Size64<RobotEntity>> {
    private static final ResourceLocation ROBOT = SoulCraft.prefixTex("entity/meta8/robot.png");

    public RobotRenderer(EntityRendererManager manager) {
        super(manager, new BipedModels.Size64<>(), 0.5f);
        addLayer(new Meta08ShieldLayer<>(this, new BipedModels.Size64<>(0.5f)));
    }

    @Override
    public ResourceLocation getTextureLocation(RobotEntity robot) {
        return ROBOT;
    }
}
