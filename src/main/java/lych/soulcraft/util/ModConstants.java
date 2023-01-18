package lych.soulcraft.util;

public final class ModConstants {
//  0 - not reversed, 1 - completely reversed
    public static final double REVERSION_INVERSE_AMOUNT = 0.9;
    public static final int VOIDWALKER_SPAWN_EGG_BACKGROUND_COLOR = 0x194746;

    private ModConstants() {}

    public static final class SoulLava {
        public static final float SOUL_LAVA_DAMAGE_MULTIPLIER = 2;
        public static final float SOUL_LAVA_R = 0.0784313f;
        public static final float SOUL_LAVA_G = 0.9294117f;
        public static final float SOUL_LAVA_B = 0.9686275f;

        private SoulLava() {}
    }

    public static final class Exa {
        public static final float ENHANCED_AUTO_JUMP_MAX_JUMP_HEIGHT_MULTIPLIER = 2;
        public static final float ENHANCED_AUTO_JUMP_COEFFICIENT = 0.19f;
        public static final float FALL_BUFFER_AMOUNT = 5;
        public static final double MONSTER_VIEW_RANGE = 16;
        public static final double BASE_TELEPORTATION_RADIUS = 19;
        public static final int TELEPORTATION_COOLDOWN = 300;
        public static final float FANGS_DAMAGE = 4;
        public static final double FANGS_SPACING = 1.25;
        public static final double FANGS_SPACING_FOR_DEFENSIVE = 0.8;
        public static final double FANGS_MAX_Y_OFFSET = 5;
        public static final int FANGS_SUMMONER_COUNT = 8;
        public static final int FANGS_SUMMONER_COUNT_FOR_DEFENSE = 5;

        private Exa() {}
    }
}
