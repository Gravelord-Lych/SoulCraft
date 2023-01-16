package lych.soulcraft.client.shader;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.entity.monster.SoulSkeletonEntity;
import lych.soulcraft.entity.monster.WandererEntity;
import net.minecraft.util.ResourceLocation;

import static net.minecraftforge.fml.client.registry.ClientRegistry.registerEntityShader;

public class ModShaders {
    public static final ResourceLocation REVERSION = SoulCraft.prefixShader(ModShaderNames.REVERSION);
    public static final ResourceLocation SOUL_MOB = SoulCraft.prefixShader(ModShaderNames.SOUL_MOB);

    public static void registerShaders() {
        registerEntityShader(SoulSkeletonEntity.class, SOUL_MOB);
        registerEntityShader(WandererEntity.class, SOUL_MOB);
    }
}
