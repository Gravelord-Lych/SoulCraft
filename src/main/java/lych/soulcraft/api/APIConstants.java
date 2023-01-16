package lych.soulcraft.api;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

final class APIConstants {
    static final Logger LOGGER = LogManager.getLogger(SoulCraftAPI.MOD_ID + "-api");
    static final Supplier<SoulCraftAPI> INSTANCES = Suppliers.memoize(() -> {
        try {
            Class<?> clazz = Class.forName("lych.soulcraft.util.impl.SoulCraftAPIImpl");
            return (SoulCraftAPI) clazz.getField("INSTANCE").get(null);
        } catch (ReflectiveOperationException e) {
            return new SoulCraftAPIDummyImpl();
        } catch (RuntimeException e) {
            LOGGER.error("No valid SoulCraftAPIImpl found, use a dummy instead");
            return new SoulCraftAPIDummyImpl();
        }
    });

    private APIConstants() {}
}
