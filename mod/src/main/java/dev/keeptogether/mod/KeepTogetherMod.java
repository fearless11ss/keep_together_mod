package dev.keeptogether.mod;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(KeepTogetherMod.MOD_ID)
public final class KeepTogetherMod {

    public static final String MOD_ID = "keeptogether";
    private static final Logger LOGGER = LogUtils.getLogger();

    public KeepTogetherMod() {
        LOGGER.info("Keep Together pre-alpha loaded. Network features are not implemented yet.");
    }
}
