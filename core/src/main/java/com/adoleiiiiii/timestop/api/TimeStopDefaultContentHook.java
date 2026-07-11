package com.adoleiiiiii.timestop.api;

import net.neoforged.bus.api.IEventBus;

/**
 * 默认内容注册扩展点；由 mod 子项目通过 ServiceLoader 提供实现。
 */
public interface TimeStopDefaultContentHook {
    /**
     * 向模组事件总线注册默认物品、实体等内容。
     *
     * @param modEventBus 模组事件总线
     */
    void registerDefaults(IEventBus modEventBus);
}
