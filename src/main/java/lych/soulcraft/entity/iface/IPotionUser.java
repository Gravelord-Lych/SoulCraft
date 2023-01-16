package lych.soulcraft.entity.iface;

import net.minecraft.entity.IRangedAttackMob;

public interface IPotionUser extends IRangedAttackMob {
    boolean isUsingPotion();

    void setUsingPotion(boolean usingPotion);
}
