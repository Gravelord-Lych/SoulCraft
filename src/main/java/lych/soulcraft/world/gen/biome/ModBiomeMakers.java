package lych.soulcraft.world.gen.biome;

import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.world.gen.carver.ModConfiguredCarvers;
import lych.soulcraft.world.gen.surface.ModConfiguredSurfaceBuilders;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Features;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilders;

public final class ModBiomeMakers {
    private ModBiomeMakers() {}

    public static Biome makeSoulBiome(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_SOUL_FIRE);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, Features.PATCH_CRIMSON_ROOTS);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_LAND);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0.4f)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x35e0eb)
                        .waterFogColor(0x052f33)
                        .fogColor(0xbffdff)
                        .skyColor(0x0849cd)
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.01f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeESVBiome() {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(0.1F)
                .scale(0.2F)
                .temperature(0.2F)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x5674B0)
                        .waterFogColor(0x0F0F36)
                        .fogColor(0xC4D0E3)
                        .skyColor(calculateSkyColor(0))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.02f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(builder.build())
                .build();
    }

    private static void soulMobs(MobSpawnInfo.Builder builder) {
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 100, 4, 4));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 15, 1, 1));
    }

    private static void addDefaultSoulBiomeCarvers(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStage.Carving.AIR, ModConfiguredCarvers.SOUL_CAVE);
    }

    private static int calculateSkyColor(float temperature) {
        float color = temperature / 3.0F;
        color = MathHelper.clamp(color, -1.0F, 1.0F);
        return MathHelper.hsvToRgb(0.62222224F - color * 0.05F, 0.5F + color * 0.1F, 1.0F);
    }
}
