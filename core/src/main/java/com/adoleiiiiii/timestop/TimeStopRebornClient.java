package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * 客户端核心入口，注册配置界面与配置重载处理。
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

    /** 断线前清除时停镜像，避免 disconnect 流程中 tick 截断引发崩溃或状态残留。 */
    @SubscribeEvent
    static void onClientLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        Time.clearClientState(Minecraft.getInstance());
    }

    /**
     * 连接已断但关卡仍在时（例如时停期间 LAN 主机退出），强制清理时停并触发原版断线流程。
     *
     * @param event 客户端 tick 末尾事件
     */
    @SubscribeEvent
    static void onClientTickPost(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (Time.isClientWorldJoinInProgress(mc) || mc.level == null || mc.isLocalServer()) {
            return;
        }
        ClientPacketListener listener = mc.getConnection();
        if (listener == null) {
            if (Time.isClientActive()) {
                Time.clearClientState(mc);
            }
            return;
        }
        if (!listener.getConnection().isConnected()) {
            Time.clearClientState(mc);
            listener.getConnection().handleDisconnection();
        }
    }
}
