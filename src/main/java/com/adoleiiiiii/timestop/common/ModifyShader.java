package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.Time;
import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.render.ShaderGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderFrameEvent;

/**
 * 每帧更新含 time uniform 的 post shader 与附魔光效 millis。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID, value = Dist.CLIENT)
public final class ModifyShader {
    public static float timeTheWorld = 0F;

    private ModifyShader() {
    }

    /** 进入时停时重置启动动画进度。 */
    public static void resetStartupAnimation() {
        timeTheWorld = 0F;
        ShaderGetter.invalidateTimeUniformCache();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderFrame(RenderFrameEvent.Pre event) {
        Time.millis++;
        if (shouldDriveStartupAnimation()) {
            timeTheWorld += TimeStopClientConfig.getStartupAnimationSpeed();
            ShaderGetter.updateUniformPost("time", timeTheWorld);
        } else {
            timeTheWorld = 0F;
            ShaderGetter.invalidateTimeUniformCache();
        }
    }

    private static boolean shouldDriveStartupAnimation() {
        return Time.isClientActive()
                && TimeStopClientConfig.isStartupAnimationEnabled()
                && ShaderGetter.hasTimeUniform();
    }
}
