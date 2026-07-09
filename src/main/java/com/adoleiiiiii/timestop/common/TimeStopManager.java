package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.Time;
import com.adoleiiiiii.timestop.network.TimeStopVisualPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.UUID;

/**
 * 服务端权威的时停状态；通过 {@link net.minecraft.server.ServerTickRateManager} 冻结世界 tick。
 */
public final class TimeStopManager {
    private static boolean active = false;
    private static UUID ownerUuid = null;

    private TimeStopManager() {
    }

    /** 切换时停；仅服务端调用。 */
    public static void toggle(MinecraftServer server, ServerPlayer player) {
        active = !active;
        ownerUuid = active ? player.getUUID() : null;
        server.tickRateManager().setFrozen(active);
        PacketDistributor.sendToAllPlayers(new TimeStopVisualPayload(active, player.getId()));
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
    public static UUID getOwnerUuid() {
        return ownerUuid;
    }
}
