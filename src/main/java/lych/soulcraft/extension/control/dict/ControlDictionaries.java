package lych.soulcraft.extension.control.dict;

import lych.soulcraft.extension.control.ControllerType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.ai.attributes.Attributes;

public final class ControlDictionaries {
    public static final ControlDictionary MIND_OPERATOR_DICT = DefaultedControlDictionary.withDefault(ControllerType.DEFAULT_MO)
            .specify(EntityType.EVOKER, ControllerType.SPEED_LIMITED_MO)
            .specify(EntityType.VEX, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.GHAST, ControllerType.GHAST_MO)
            .specify(EntityType.PHANTOM, ControllerType.AGGRESSIVE_FLYER_MO)
            .specify(EntityType.PARROT, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BEE, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .specify(EntityType.BAT, ControllerType.FLYER_MO)
            .specify(EntityType.BLAZE, ControllerType.BLAZE_MO)
            .specify(EntityType.VILLAGER, ControllerType.HARMLESS_SPEED_LIMITED_MO)
            .addCondition(mob -> mob.getAttribute(Attributes.ATTACK_DAMAGE) == null, ControllerType.HARMLESS_MO)
            .addCondition(mob -> mob.getAttribute(Attributes.FLYING_SPEED) != null, ControllerType.SPEED_INDEPENDENT_FLYER_MO)
            .addCondition(mob -> mob instanceof FlyingEntity, ControllerType.FLYER_MO)
            .build();

    private ControlDictionaries() {}
}
