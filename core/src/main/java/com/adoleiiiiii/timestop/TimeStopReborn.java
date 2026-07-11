package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopDefaultContentHook;
import com.adoleiiiiii.timestop.common.SoundsRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.config.TimeStopCommonConfig;
import com.adoleiiiiii.timestop.network.TimeStopPackets;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

import java.util.ServiceLoader;

/**
 * 时停核心模组入口，负责注册配置、网络包、时停音效与默认内容桥接。
 */
@Mod(TimeStopReborn.MODID)
public class TimeStopReborn {
    public static final String MODID = "timestopreborn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TimeStopReborn(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TimeStopClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, TimeStopCommonConfig.SPEC);
        modEventBus.addListener(TimeStopPackets::register);
        modEventBus.addListener(TimeStopRebornClient::onConfigReload);
        SoundsRegister.SOUNDS.register(modEventBus);
        ServiceLoader.load(TimeStopDefaultContentHook.class).findFirst()
                .ifPresent(hook -> hook.registerDefaults(modEventBus));
    }
}
