package com.adoleiiiiii.timestop.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 服务端时停状态变更后发布；在 S2C 广播之后触发。
 */
public class TimeStopStateChangeEvent extends Event {
    private final MinecraftServer server;
    private final ServerPlayer player;
    private final boolean active;
    private final boolean previousActive;

    /**
     * 构造时停状态变更事件。
     *
     * @param server         当前服务端
     * @param player         触发本次变更的玩家
     * @param active         变更后是否处于时停
     * @param previousActive 变更前是否处于时停
     */
    public TimeStopStateChangeEvent(MinecraftServer server, ServerPlayer player, boolean active, boolean previousActive) {
        this.server = server;
        this.player = player;
        this.active = active;
        this.previousActive = previousActive;
    }

    /**
     * 返回事件所属的服务端。
     *
     * @return 当前服务端实例
     */
    public MinecraftServer getServer() {
        return server;
    }

    /**
     * 返回触发本次状态变更的玩家。
     *
     * @return 触发玩家
     */
    public ServerPlayer getPlayer() {
        return player;
    }

    /**
     * 返回变更后的时停状态。
     *
     * @return 变更后是否处于时停
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 返回变更前的时停状态。
     *
     * @return 变更前是否处于时停
     */
    public boolean wasActive() {
        return previousActive;
    }

    /**
     * 返回时停触发者 UUID；未激活时不存在有效触发者。
     *
     * @return 时停触发者 UUID，未激活时为 null
     */
    @Nullable
    public UUID getOwnerUuid() {
        return active ? player.getUUID() : null;
    }
}
