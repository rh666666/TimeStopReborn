package com.adoleiiiiii.timestop.api;

import com.adoleiiiiii.timestop.common.TimeStopManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 时停模组对外 API：查询与切换时停状态。
 */
public final class TimeStopAPI {
    private TimeStopAPI() {
    }

    /**
     * 按维度上下文查询时停是否生效。
     *
     * @param level 当前维度
     * @return 客户端读镜像状态，服务端读权威状态
     */
    public static boolean isActive(Level level) {
        return TimeStopManager.isActive(level);
    }

    /**
     * 查询服务端权威时停状态。
     *
     * @param server 当前服务端
     * @return 是否处于时停
     */
    public static boolean isActive(MinecraftServer server) {
        return TimeStopManager.isActive();
    }

    /**
     * 切换时停；仅服务端调用。
     *
     * @param server 当前服务端
     * @param player 触发玩家
     * @return 切换后是否处于时停
     */
    public static boolean toggle(MinecraftServer server, ServerPlayer player) {
        return TimeStopManager.toggle(server, player);
    }

    /**
     * 显式设置时停状态；仅服务端调用。
     *
     * @param server 当前服务端
     * @param player 触发玩家
     * @param active 目标状态
     * @return 设置后是否处于时停
     */
    public static boolean setActive(MinecraftServer server, ServerPlayer player, boolean active) {
        return TimeStopManager.setActive(server, player, active);
    }

    /**
     * 获取当前时停触发者 UUID。
     *
     * @param server 当前服务端
     * @return 未激活时为 null
     */
    @Nullable
    public static UUID getOwnerUuid(MinecraftServer server) {
        return TimeStopManager.getOwnerUuid();
    }

    /**
     * 获取当前时停触发者玩家实体。
     *
     * @param server 当前服务端
     * @return 未激活或玩家离线时为 null
     */
    @Nullable
    public static ServerPlayer getOwner(MinecraftServer server) {
        return TimeStopManager.getOwner(server);
    }
}
