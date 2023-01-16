package lych.soulcraft;

import lych.soulcraft.config.CommonConfig;
import lych.soulcraft.entity.ModAttributes;
import lych.soulcraft.item.crafting.ModRecipeSerializers;
import lych.soulcraft.potion.ModPotions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SoulCraft.MOD_ID)
public class SoulCraft {
    public static final String LYCH233 = "Lych233";
    public static final String MOD_ID = "soulcraft";
    public static final String MOD_NAME = "Soul Craft";
    public static final String SHORTENED_MOD_ID = "sc";
    public static final String SHORTENED_MOD_NAME = "SC";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SoulCraft() {
        SoulCraft.LOGGER.debug("Registering Configuration..");
        SharedConstants.IS_RUNNING_IN_IDE = true;
        SharedConstants.CHECK_DATA_FIXER_SCHEMA = false;
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        ModRecipeSerializers.RECIPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModAttributes.ATTRIBUTES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModPotions.POTIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static <T extends ForgeRegistryEntry<T>> T make(T value, String name) {
        return value.setRegistryName(MOD_ID, name);
    }

    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

    public static ResourceLocation prefixTex(String name) {
        return new ResourceLocation(MOD_ID, "textures/" + name);
    }

    public static String prefixMsg(String name) {
        return prefixMsg("message", name);
    }

    public static String prefixMsg(String type, String name) {
        return type + "." + MOD_ID + "." + name;
    }

    public static String prefixData(String name) {
        return MOD_ID + "." + name;
    }

    public static String prefixKeyMessage(String name) {
        return String.format("key.message.%s.%s", MOD_ID, name);
    }

    public static String prefixKeyCategory(String name) {
        return String.format("key.category.%s.%s", MOD_ID, name);
    }

    public static ResourceLocation prefixShader(String name) {
        return SoulCraft.prefix(String.format("shaders/post/%s.json", name));
    }
}
