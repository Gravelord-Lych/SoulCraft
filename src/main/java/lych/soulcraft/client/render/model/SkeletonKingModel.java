package lych.soulcraft.client.render.model;

import lych.soulcraft.entity.monster.boss.SkeletonKingEntity;
import net.minecraft.client.renderer.entity.model.SkeletonModel;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkeletonKingModel<T extends SkeletonKingEntity> extends SkeletonModel<T> {
    public SkeletonKingModel() {
        this(0, false);
    }

    public SkeletonKingModel(float posOffset, boolean noModel) {
        super(posOffset, noModel);
    }

    @Override
    public void setupAnim(T skeleton, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(skeleton, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        if (skeleton.isCastingSpell()) {
            rightArm.z = 0.0F;
            rightArm.x = -5.0F;
            leftArm.z = 0.0F;
            leftArm.x = 5.0F;
            rightArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            leftArm.xRot = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
            rightArm.zRot = 2.3561945F;
            leftArm.zRot = -2.3561945F;
            rightArm.yRot = 0.0F;
            leftArm.yRot = 0.0F;
        }
    }
}
