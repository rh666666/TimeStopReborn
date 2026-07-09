package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.Time;
import com.adoleiiiiii.timestop.api.TimeStopStateChangeEvent;
import com.adoleiiiiii.timestop.network.TimeStopVisualPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 服务端权威的时停状态；通过 {@link net.minecraft.server.ServerTickRateManager} 冻结世界 tick。
 */
public final class TimeStopManager {
    private static boolean active = false;
    @Nullable
    private static UUID ownerUuid = null;
    private static int ownerEntityId = -1;

    private TimeStopManager() {
    }

    /**
     * 切换时停；仅服务端调用。
     *
     * @return 切换后是否处于时停
     */
    public static boolean toggle(MinecraftServer server, ServerPlayer player) {
        return setActive(server, player, !active);
    }

    /**
     * 显式设置时停状态；仅服务端调用。
     *
     * @return 设置后是否处于时停
     */
    public static boolean setActive(MinecraftServer server, ServerPlayer player, boolean targetActive) {
        if (active == targetActive) {
            return active;
        }
        boolean previousActive = active;
        active = targetActive;
        ownerUuid = targetActive ? player.getUUID() : null;
        ownerEntityId = targetActive ? player.getId() : -1;
        server.tickRateManager().setFrozen(active);
        PacketDistributor.sendToAllPlayers(new TimeStopVisualPayload(active, ownerEntityId, ownerUuid));
        NeoForge.EVENT_BUS.post(new TimeStopStateChangeEvent(server, player, active, previousActive));
        return active;
    }

    /** @return 服务端是否处于时停 */
    public static boolean isActive() {
        return active;
    }

    /**
     * 按维度上下文查询时停是否生效。
     *
     * @param level 当前维度
     * @return 客户端读镜像状态，服务端读权威状态
     */
    public static boolean isActive(Level level) {
        if (level.isClientSide()) {
            return Time.isClientActive();
        }
        return active;
    }

    /** @return 当前时停触发者 UUID，未激活时为 null */
    @Nullable
    public static UUID getOwnerUuid() {
        return ownerUuid;
    }

    /** @return 触发者实体 id，未激活时为 -1 */
    public static int getOwnerEntityId() {
        return ownerEntityId;
    }

    /**
     * 解析当前在线的触发者玩家。
     *
     * @param server 当前服务端
     * @return 未激活或玩家离线时为 null
     */
    @Nullable
    public static ServerPlayer getOwner(MinecraftServer server) {
        if (!active || ownerUuid == null) {
            return null;
        }
        return server.getPlayerList().getPlayer(ownerUuid);
    }
}
