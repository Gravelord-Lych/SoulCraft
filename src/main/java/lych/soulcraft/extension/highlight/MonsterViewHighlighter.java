package lych.soulcraft.extension.highlight;

import lych.soulcraft.api.shield.ISharedShield;
import lych.soulcraft.api.shield.IShieldUser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

import java.awt.Color;
import java.util.UUID;

public class MonsterViewHighlighter extends AbstractHighlighter {
    public MonsterViewHighlighter(UUID entityUUID, long highlightTicksRemaining) {
        super(entityUUID, highlightTicksRemaining);
    }

    public MonsterViewHighlighter(UUID entityUUID, CompoundNBT compoundNBT) {
        super(entityUUID, compoundNBT);
    }

    @Override
    protected Color getColor(ServerWorld level, Entity entity) {
        if (entity instanceof LivingEntity) {
            LivingEntity living = (LivingEntity) entity;
            float hue = MathHelper.lerp(living.getHealth() / living.getMaxHealth(), 0, 0.05f);
            if (living.getAbsorptionAmount() > 0) {
                hue += Math.min(MathHelper.lerp(living.getAbsorptionAmount() / living.getMaxHealth(), 0, 0.05f), 0.1f);
            }
            if (living instanceof IShieldUser && ((IShieldUser) living).getSharedShield() != null && ((IShieldUser) living).isShieldValid()) {
                ISharedShield shield = ((IShieldUser) living).getSharedShield();
                hue = periodicallyLerp(1 - shield.getHealth() / shield.getPassiveDefense(), 0.5833333f, hue);
            }
            float saturation = 0.8f;
            float brightness = MathHelper.lerp(living.getHealth() / living.getMaxHealth(), 0.5f, 0.9f);
            return new Color(Color.HSBtoRGB(hue, saturation, brightness));
        }
        return Color.RED;
    }

    private static float periodicallyLerp(float amount, float min, float max) {
        float value = MathHelper.lerp(amount, min, max + 1);
        if (value >= 1) {
            value--;
        }
        return value;
    }

    @Override
    public HighlighterType getType() {
        return HighlighterType.MONSTER_VIEW;
    }

    @Override
    public int getPriority() {
        return 2000;
    }
}
