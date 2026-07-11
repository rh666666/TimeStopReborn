package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.UUID;

/**
 * 服务端生命周期钩子：停服或时停触发者离线时解除冻结，避免玩家无法正常断开。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID)
public final class TimeStopLifecycleHandler {
    private TimeStopLifecycleHandler() {
    }

    /**
     * 停服前解除 tick 冻结并通知客户端关闭时停视觉。
     *
     * @param event 服务端停止事件
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        TimeStopManager.resetOnServerStop(event.getServer());
    }

    /**
     * 时停触发者离线时提前解除冻结并同步客户端，避免 LAN 主机退出后客户端仍留在世界中。
     *
     * @param event 玩家登出事件
     */
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!TimeStopManager.isActive()) {
            return;
        }
        UUID ownerUuid = TimeStopManager.getOwnerUuid();
        if (ownerUuid != null && ownerUuid.equals(player.getUUID())) {
            TimeStopManager.resetOnServerStop(player.server);
        }
    }
}
