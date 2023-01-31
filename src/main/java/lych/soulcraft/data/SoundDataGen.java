package lych.soulcraft.data;

import com.google.common.base.Preconditions;
import lych.soulcraft.SoulCraft;
import lych.soulcraft.util.DefaultValues;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;
import net.minecraftforge.fml.RegistryObject;

import java.util.Objects;
import java.util.function.Consumer;

import static lych.soulcraft.util.ModSoundEvents.*;

public class SoundDataGen extends SoundDefinitionsProvider {
    private static final String GENERIC_FOOTSTEPS = "subtitles.block.generic.footsteps";
    private static final String BOW_PATH = "random/bow";
    private static final ResourceLocation LASER_PATH = SoulCraft.prefix("random/laser");

    public SoundDataGen(DataGenerator generator, ExistingFileHelper helper) {
        super(generator, SoulCraft.MOD_ID, helper);
    }

    @Override
    public void registerSounds() {
        add(DEFENSIVE_META8_SHARE_SHIELD);
        multiple(ENERGY_SOUND_BREAK, "random/explode1", "random/explode2", "random/explode3", "random/explode4");
        redirect(META8_LASER, LASER_PATH, 3);
        add(META8_SHARE_SHIELD);
        single(ROBOT_DEATH, "mob/irongolem/death");
        multiple(ROBOT_HURT, "mob/irongolem/hit1", "mob/irongolem/hit2", "mob/irongolem/hit3", "mob/irongolem/hit4");
        multiple(ROBOT_STEP, def -> def.subtitle(GENERIC_FOOTSTEPS), "mob/irongolem/walk1", "mob/irongolem/walk2", "mob/irongolem/walk3", "mob/irongolem/walk4");
        add(SOUL_SKELETON_AMBIENT, 3);
        add(SOUL_SKELETON_DEATH);
        add(SOUL_SKELETON_HURT, 4);
        redirect(SOUL_SKELETON_SHOOT, BOW_PATH);
        add(SOUL_SKELETON_STEP, 4, def -> def.subtitle(GENERIC_FOOTSTEPS));
        redirect(WANDERER_LASER, LASER_PATH, 3);
    }

    @Override
    public String getName() {
        return "Sounds: " + SoulCraft.MOD_ID;
    }

    private void single(RegistryObject<SoundEvent> sound,  String name) {
        single(sound, DefaultValues.dummyConsumer(), name);
    }

    private void single(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, String name) {
        add(sound, Util.make(definition().with(sound(name)).subtitle(makeSubtitle(sound)), additionalOperations::accept));
    }

    private void multiple(RegistryObject<SoundEvent> sound,  String... names) {
        multiple(sound, DefaultValues.dummyConsumer(), names);
    }

    private void multiple(RegistryObject<SoundEvent> sound, Consumer<? super SoundDefinition> additionalOperations, String... names) {
        Objects.requireNonNull(names);
        Preconditions.checkArgument(names.length > 1);
        SoundDefinition definition = definition().subtitle(makeSubtitle(sound));
        for (String name : names) {
            definition.with(sound(name));
        }
        additionalOperations.accept(definition);
        add(sound, definition);
    }

    private void add(RegistryObject<SoundEvent> sound) {
        add(sound, 1);
    }

    private void add(RegistryObject<SoundEvent> sound, int count) {
        add(sound, count, DefaultValues.dummyConsumer());
    }

    private void add(RegistryObject<SoundEvent> sound, int count, Consumer<? super SoundDefinition> additionalOperations) {
        Preconditions.checkArgument(count > 0, "Count must be positive");
        SoundDefinition definition = definition();
        if (count > 1) {
            for (int i = 1; i <= count; i++) {
                definition.with(sound((sound.getId() + String.valueOf(i)).replace('.', '/')));
            }
        } else {
            definition.with(sound(sound.getId().toString().replace('.', '/')));
        }
        definition.subtitle(makeSubtitle(sound));
        additionalOperations.accept(definition);
        add(sound, definition);
    }

    private void redirect(RegistryObject<SoundEvent> sound, String redirectTarget) {
        redirect(sound, new ResourceLocation(redirectTarget));
    }

    private void redirect(RegistryObject<SoundEvent> sound, String redirectTarget, int count) {
        redirect(sound, new ResourceLocation(redirectTarget), count);
    }

    private void redirect(RegistryObject<SoundEvent> sound, ResourceLocation redirectTarget) {
        redirect(sound, redirectTarget, 1);
    }

    private void redirect(RegistryObject<SoundEvent> sound, ResourceLocation redirectTarget, int count) {
        SoundDefinition definition = definition().subtitle(makeSubtitle(sound));
        if (count > 1) {
            for (int i = 1; i <= count; i++) {
                definition.with(sound(redirectTarget + String.valueOf(i)));
            }
        } else {
            definition.with(sound(redirectTarget));
        }
        add(sound, definition);
    }

    private static String makeSubtitle(RegistryObject<SoundEvent> sound) {
        return "subtitles." + sound.getId();
    }
}
