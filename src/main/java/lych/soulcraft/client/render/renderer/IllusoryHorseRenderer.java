package lych.soulcraft.client.render.renderer;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.client.render.model.IllusoryHorseModel;
import lych.soulcraft.entity.passive.IllusoryHorseEntity;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class IllusoryHorseRenderer extends AbstractHorseRenderer<IllusoryHorseEntity, IllusoryHorseModel<IllusoryHorseEntity>> {
    private static final ResourceLocation ILLUSORY_HORSE = SoulCraft.prefixTex("entity/esv/illusory_horse.png");
    private static final ResourceLocation ILLUSORY_HORSE_ETHEREAL = SoulCraft.prefixTex("entity/esv/illusory_horse_ethereal.png");

    public IllusoryHorseRenderer(EntityRendererManager manager) {
        super(manager, new IllusoryHorseModel<>(0), 1);
    }

    @Override
    public ResourceLocation getTextureLocation(IllusoryHorseEntity horse) {
        return horse.isEthereal() ? ILLUSORY_HORSE_ETHEREAL : ILLUSORY_HORSE;
    }
}
