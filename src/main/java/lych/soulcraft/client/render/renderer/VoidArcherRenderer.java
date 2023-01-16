package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.monster.voidwalker.VoidArcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VoidArcherRenderer extends AbstractVoidwalkerRenderer<VoidArcherEntity> {
    private static final ResourceLocation VOID_ARCHER_CLOTHES = SoulCraft.prefixTex("entity/esv/void_archer_clothes.png");

    public VoidArcherRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public ResourceLocation getOuterLayer(VoidArcherEntity archer) {
        return VOID_ARCHER_CLOTHES;
    }
}
