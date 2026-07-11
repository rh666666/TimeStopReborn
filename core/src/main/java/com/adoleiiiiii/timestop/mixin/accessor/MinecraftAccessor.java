package com.adoleiiiiii.timestop.mixin.accessor;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露客户端关卡卸载标志，供时停判定是否可拦截 tick。
 */
@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    /** @return 是否正在断开连接并卸载 ClientLevel */
    @Accessor("clientLevelTeardownInProgress")
    boolean isClientLevelTeardownInProgress();

    /** @return 渲染帧使用的 DeltaTracker 计时器 */
    @Accessor("timer")
    DeltaTracker.Timer getRenderTimer();
}
