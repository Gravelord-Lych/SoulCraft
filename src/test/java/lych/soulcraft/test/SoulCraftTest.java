package lych.soulcraft.test;

import lych.soulcraft.SoulCraft;
import lych.soulcraft.api.SoulCraftAPI;
import lych.soulcraft.util.impl.SoulCraftAPIImpl;
import net.minecraft.util.SharedConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SoulCraftTest {
    @Test
    public void testModid() {
        Assertions.assertEquals(SoulCraftAPI.MOD_ID, SoulCraft.MOD_ID);
    }

    @Test
    public void testAPI() {
        Assertions.assertSame(SoulCraftAPI.getInstance(), SoulCraftAPIImpl.INSTANCE);
    }

    @Test
    public void testEnvironment() {
        Assertions.assertFalse(SharedConstants.IS_RUNNING_IN_IDE);
        Assertions.assertTrue(SharedConstants.CHECK_DATA_FIXER_SCHEMA);
    }
}
