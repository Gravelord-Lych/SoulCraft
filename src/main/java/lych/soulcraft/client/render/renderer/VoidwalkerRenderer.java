package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.monster.voidwalker.VoidwalkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidwalkerRenderer extends AbstractVoidwalkerRenderer<VoidwalkerEntity> {
    private static final ResourceLocation VOIDWALKER_CLOTHES = SoulCraft.prefixTex("entity/esv/voidwalker_clothes.png");

    public VoidwalkerRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(VoidwalkerEntity voidwalker) {
        return VOIDWALKER_CLOTHES;
    }
}
