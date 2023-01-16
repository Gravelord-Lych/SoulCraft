package lych.soulcraft.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.util.ResourceLocation;

public class IllusoryHorseModel<T extends AbstractHorseEntity> extends HorseModel<T> {
    public IllusoryHorseModel(float offset) {
        super(offset);
    }

    @Override
    public RenderType renderType(ResourceLocation location) {
        return RenderType.entityTranslucent(location);
    }
}
