package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.render.entity.KnifeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 客户端专用入口，注册实体渲染器并处理配置重载。
 */
@Mod(value = TimeStopReborn.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TimeStopReborn.MODID, value = Dist.CLIENT)
public class TimeStopRebornClient {
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegister.FLYING_SWORD.get(), KnifeRenderer::new);
    }

    /** 配置热重载时，若处于时停则按新配置切换或关闭 post 滤镜。 */
    static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TimeStopClientConfig.SPEC) {
            Time.reloadPostEffectFromConfig();
        }
    }
}
