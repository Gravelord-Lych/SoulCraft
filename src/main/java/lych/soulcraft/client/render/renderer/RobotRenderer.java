package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.BipedModels;
import lych.soulcraft.client.render.renderer.layer.EnergyShieldLayer;
import lych.soulcraft.entity.monster.RobotEntity;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RobotRenderer extends BipedRenderer<RobotEntity, BipedModels.Size64<RobotEntity>> {
    private static final ResourceLocation ROBOT = SoulCraft.prefixTex("entity/meta8/robot.png");

    public RobotRenderer(EntityRendererManager manager) {
        super(manager, new BipedModels.Size64<>(), 0.5f);
        addLayer(new EnergyShieldLayer<>(this, new BipedModels.Size64<>(0.5f)));
    }

    @Override
    public ResourceLocation getTextureLocation(RobotEntity robot) {
        return ROBOT;
    }
}
