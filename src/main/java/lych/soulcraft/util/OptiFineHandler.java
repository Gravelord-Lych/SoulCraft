package lych.soulcraft.util;

import com.google.common.base.Suppliers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class OptiFineHandler {
    private static final Logger LOGGER = LogManager.getLogger("soulcraft-optihandler");
    private static final Supplier<Boolean> OPTIFINE_PRESENT = Suppliers.memoize(() -> {
        try {
            Class.forName("net.optifine.Config");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    });

    public static boolean isOptiFineLoaded() {
        return OPTIFINE_PRESENT.get();
    }
}
