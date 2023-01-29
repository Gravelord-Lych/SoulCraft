package lych.soulcraft.entity.monster.voidwalker;

import com.google.common.collect.ImmutableList;
import lych.soulcraft.util.WeightedRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static lych.soulcraft.entity.monster.voidwalker.AbstractVoidLasererEntity.prefixTex;

public final class EtheArmorerAttackType implements WeightedRandom.Item {
    private static final ResourceLocation SGA_TEXTURE = prefixTex("ethe_armorer_enchant_beam.png");
    private static final ResourceLocation TEXTURE = prefixTex("ethe_armorer_destroy_beam.png");
    private static final ResourceLocation WOODIFY_TEXTURE = prefixTex("ethe_armorer_woodify_beam.png");
    public static final EtheArmorerAttackType CURSE = new EtheArmorerAttackType(SGA_TEXTURE, 60, 0x830035, 0xA80049, EtheArmorerEntity::canCurse);
    public static final EtheArmorerAttackType DAMAGE = new EtheArmorerAttackType(TEXTURE, 100, 0x6F0000, 0x8F0000, EtheArmorerEntity::canDamage);
    public static final EtheArmorerAttackType RECONSTRUCT = new EtheArmorerAttackType(TEXTURE, 100, 0x80FFFF, 0x80FFFF, EtheArmorerEntity::canReconstruct);
    public static final EtheArmorerAttackType REINFORCE = new EtheArmorerAttackType(SGA_TEXTURE, 100, 0xDDDDDD, 0xEEEEEE, EtheArmorerEntity::canReinforce);
    public static final EtheArmorerAttackType RENAME = new EtheArmorerAttackType(TEXTURE, 5, 0xCEAC6D, 0xE6C78C, EtheArmorerEntity::canRename);
    public static final EtheArmorerAttackType WOODIFY = new EtheArmorerAttackType(WOODIFY_TEXTURE, 10, 0x755821, 0x866526, EtheArmorerEntity::canWoodify);
    private static List<EtheArmorerAttackType> ATTACK_TYPES;
    private static int nextId;

    private final int id;
    private final int weight;
    private final ResourceLocation textureLocation;
    private final int srcColor;
    private final int destColor;
    private final BiPredicate<? super EtheArmorerEntity, ? super LivingEntity> canUsePredicate;

    public EtheArmorerAttackType(ResourceLocation textureLocation, int weight, int srcColor, int destColor, BiPredicate<? super EtheArmorerEntity, ? super LivingEntity> canUsePredicate) {
        this.canUsePredicate = canUsePredicate;
        if (ATTACK_TYPES == null) {
            ATTACK_TYPES = new ArrayList<>();
        }
        this.weight = weight;
        this.textureLocation = textureLocation;
        this.srcColor = srcColor;
        this.destColor = destColor;
        ATTACK_TYPES.add(this);
        id = nextId++;
    }

    @Override
    public int getWeight() {
        return weight;
    }

    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }

    public int getSrcColor() {
        return srcColor;
    }

    public int getDestColor() {
        return destColor;
    }

    public int getId() {
        return id;
    }

    public boolean canUse(EtheArmorerEntity armorer, LivingEntity target) {
        return canUsePredicate.test(armorer, target);
    }

    @Nullable
    public static EtheArmorerAttackType byId(int id) {
        if (id >= 0 && id < ATTACK_TYPES.size()) {
            return ATTACK_TYPES.get(id);
        }
        return null;
    }

    public static ImmutableList<EtheArmorerAttackType> getAttackTypes() {
        return ImmutableList.copyOf(ATTACK_TYPES);
    }
}
