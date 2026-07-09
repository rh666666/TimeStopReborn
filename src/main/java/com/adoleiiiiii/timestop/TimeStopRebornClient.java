package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.render.entity.KnifeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端专用入口，注册实体渲染器、配置界面与配置重载处理。
 */
@Mod(value = TimeStopReborn.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = TimeStopReborn.MODID, value = Dist.CLIENT)
public class TimeStopRebornClient {
    /**
     * 注册 NeoForge 通用配置界面，供模组列表「配置」按钮打开。
     *
     * @param container 本模组客户端容器
     */
    public TimeStopRebornClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!TimeStopFeatureGate.registerDefaultContent() || !EntityRegister.FLYING_SWORD.isBound()) {
            return;
        }
        event.registerEntityRenderer(EntityRegister.FLYING_SWORD.get(), KnifeRenderer::new);
    }

    /** 配置热重载时，若处于时停则按新配置切换或关闭 post 滤镜。 */
    static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TimeStopClientConfig.SPEC) {
            Time.reloadPostEffectFromConfig();
        }
    }

    @SubscribeEvent
    static void onClientLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        Time.applyPendingVisuals();
    }

    @SubscribeEvent
    static void onClientClone(ClientPlayerNetworkEvent.Clone event) {
        Time.applyPendingVisuals();
    }
}
