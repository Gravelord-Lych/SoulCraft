package lych.soulcraft.entity;

import lych.soulcraft.SoulCraft;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, SoulCraft.MOD_ID);
    public static final RegistryObject<Attribute> JUMP_STRENGTH = ATTRIBUTES.register("jump_strength", () -> new RangedAttribute(SoulCraft.MOD_ID + ".jump_strength", 0.42, 0, 1024).setSyncable(true));
}
