package com.adoleiiiiii.timestop.content;

import com.adoleiiiiii.timestop.api.TimeStopDefaultContentHook;
import net.neoforged.bus.api.IEventBus;

/**
 * 通过 ServiceLoader 向 core 暴露默认内容注册。
 */
public final class TimeStopDefaultContentHookImpl implements TimeStopDefaultContentHook {
    @Override
    public void registerDefaults(IEventBus modEventBus) {
        TimeStopDefaultContent.register(modEventBus);
    }
}
