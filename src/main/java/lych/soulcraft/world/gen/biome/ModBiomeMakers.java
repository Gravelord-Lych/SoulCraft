package lych.soulcraft.world.gen.biome;

import lych.soulcraft.entity.ModEntities;
import lych.soulcraft.world.gen.carver.ModConfiguredCarvers;
import lych.soulcraft.world.gen.feature.ModConfiguredFeatures;
import lych.soulcraft.world.gen.surface.ModConfiguredSurfaceBuilders;
import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
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
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.SL_PATCH_SOUL_FIRE);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_SOUL_WART);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_LAND);

        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0)
                .specialEffects(defaultSoulBiomeAmbience(0.01f).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeSoulLavaOcean(float depth, float scale) {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_LAVA_OCEAN);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(depth)
                .scale(scale)
                .temperature(2.5f)
                .downfall(0)
                .specialEffects(defaultSoulBiomeAmbience(0.005f).build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(builder.build())
                .build();
    }

    private static BiomeAmbience.Builder defaultSoulBiomeAmbience(float prob) {
        return new BiomeAmbience.Builder()
                .waterColor(0x35e0eb)
                .waterFogColor(0x052f33)
                .fogColor(0xbffdff)
                .skyColor(0x0849cd)
                .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, prob))
                .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS);
    }

    public static Biome makeESVBiome() {
        BiomeGenerationSettings.Builder builder = new BiomeGenerationSettings.Builder().surfaceBuilder(ConfiguredSurfaceBuilders.NOPE);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(0.1f)
                .scale(0.2f)
                .temperature(0.2f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x5674B0)
                        .waterFogColor(0x0F0F36)
                        .fogColor(0xC4D0E3)
                        .skyColor(calculateSkyColor(0.2f))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.02f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(builder.build())
                .build();
    }

    public static Biome makeParchedDesertBiome(float depth, float scale) {
        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_INFERNO);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.PARCHED_DESERT);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.NONE)
                .depth(depth)
                .scale(scale)
                .temperature(2.5f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x661717)
                        .waterFogColor(0x562626)
                        .fogColor(0xCE5E5E)
                        .skyColor(calculateFantasticalBiomeSkyColor(2.5f))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.SMOKE, 0.002f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS)
                        .build())
                .mobSpawnSettings(MobSpawnInfo.EMPTY)
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeWarpedBiome(float depth, float scale) {
        return makeWarpedBiome(depth, scale, false);
    }

    public static Biome makeWarpedBiome(float depth, float scale, boolean edge) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        spawnBuilder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 100, 2, 2))
                .addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 5, 1, 1))
                .addMobCharge(EntityType.ENDERMAN, 1, 0.12);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.WARPED_PLAINS);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_POISONOUS_FIRE);
        DefaultBiomeFeatures.addDefaultMushrooms(genBuilder);
        genBuilder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.WARPED_FOREST_VEGETATION);
        if (!edge) {
            genBuilder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_WARPED_FUNGI)
                    .addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, Features.NETHER_SPROUTS)
                    .addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_TWISTING_VINE);
        } else {
            genBuilder.addFeature(GenerationStage.Decoration.VEGETAL_DECORATION, ModConfiguredFeatures.SL_WARPED_FUNGI_AT_THE_EDGE);
        }
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(2)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x3f76e4)
                        .waterFogColor(0x050533)
                        .fogColor(edge ? 0x11051a : 0x1a051a)
                        .skyColor(calculateSkyColor(2))
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.WARPED_SPORE, 0.02857f))
                        .ambientLoopSound(SoundEvents.AMBIENT_WARPED_FOREST_LOOP)
                        .ambientMoodSound(new MoodSoundAmbience(SoundEvents.AMBIENT_WARPED_FOREST_MOOD, 6000, 8, 2))
                        .ambientAdditionsSound(new SoundAdditionsAmbience(SoundEvents.AMBIENT_WARPED_FOREST_ADDITIONS, 0.0111))
                        .backgroundMusic(BackgroundMusicTracks.createGameMusic(SoundEvents.MUSIC_BIOME_WARPED_FOREST))
                        .build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeInnermostSoulLand(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_PURE_SOUL_FIRE);
        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_SOUL_WART);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.INNERMOST_SOUL_LAND);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.PLAINS)
                .depth(depth)
                .scale(scale)
                .temperature(1.8f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x353eeb)
                        .waterFogColor(0x051033)
                        .fogColor(0xbfccff)
                        .skyColor(0x0818cd)
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.006f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    public static Biome makeSoulBeach(float depth, float scale) {
        MobSpawnInfo.Builder spawnBuilder = new MobSpawnInfo.Builder();
        soulMobs(spawnBuilder);

        BiomeGenerationSettings.Builder genBuilder = new BiomeGenerationSettings.Builder();
        addDefaultSoulBiomeCarvers(genBuilder);

        genBuilder.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, ModConfiguredFeatures.PATCH_SOUL_WART);
        genBuilder.surfaceBuilder(ModConfiguredSurfaceBuilders.SOUL_BEACH);
        return new Biome.Builder()
                .precipitation(Biome.RainType.NONE)
                .biomeCategory(Biome.Category.BEACH)
                .depth(depth)
                .scale(scale)
                .temperature(1.8f)
                .downfall(0)
                .specialEffects(new BiomeAmbience.Builder()
                        .waterColor(0x353eeb)
                        .waterFogColor(0x051033)
                        .fogColor(0xbfccff)
                        .skyColor(0x0818cd)
                        .ambientParticle(new ParticleEffectAmbience(ParticleTypes.ASH, 0.01f))
                        .ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS).build())
                .mobSpawnSettings(spawnBuilder.build())
                .generationSettings(genBuilder.build())
                .build();
    }

    private static void soulMobs(MobSpawnInfo.Builder builder) {
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(ModEntities.SOUL_SKELETON, 100, 4, 4));
        builder.addSpawn(EntityClassification.MONSTER, new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 10, 1, 1));
    }

    private static void addDefaultSoulBiomeCarvers(BiomeGenerationSettings.Builder builder) {
        builder.addCarver(GenerationStage.Carving.AIR, ModConfiguredCarvers.SOUL_CAVE);
    }

    private static int calculateSkyColor(float temperature) {
        float colorFactor = temperature / 3f;
        colorFactor = MathHelper.clamp(colorFactor, -1, 1);
        return MathHelper.hsvToRgb(0.62222224F - colorFactor * 0.05F, 0.5F + colorFactor * 0.1F, 1);
    }

    private static int calculateFantasticalBiomeSkyColor(float temperature) {
        float colorFactor = (temperature - 0.5f) / 2;
        colorFactor = MathHelper.clamp(colorFactor, -1, 1);
        return MathHelper.hsvToRgb(0.33333333f - colorFactor * 0.3f, Math.min(0.05f + colorFactor * colorFactor, 1), 1);
    }
}
