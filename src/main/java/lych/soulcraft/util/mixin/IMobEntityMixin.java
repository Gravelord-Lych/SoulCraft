package lych.soulcraft.util.mixin;

import lych.soulcraft.extension.soulpower.control.ControlledMobData;
import lych.soulcraft.extension.soulpower.control.controller.Adjustment;
import net.minecraft.inventory.EquipmentSlotType;

import java.util.List;
import java.util.UUID;

public interface IMobEntityMixin {
    boolean hasValidBrain();

    boolean isControlled();

    List<Adjustment.AdjInstance> getAdjustments();

    void addAdjustment(Adjustment<?> adjustment, ControlledMobData data);

    boolean hasAdjustments();

    void removeAllAdjustments();

    void removeAllAdjustments(UUID mobCausedAdjustment);

    float callGetEquipmentDropChance(EquipmentSlotType slotType);
}
