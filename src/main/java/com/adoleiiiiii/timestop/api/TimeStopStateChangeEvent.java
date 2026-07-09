package com.adoleiiiiii.timestop.api;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * 服务端时停状态变更后发布；在 S2C 广播之后触发。
 */
public class TimeStopStateChangeEvent extends Event {
    private final MinecraftServer server;
    private final ServerPlayer player;
    private final boolean active;
    private final boolean previousActive;

    /**
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

    /** @return 当前服务端 */
    public MinecraftServer getServer() {
        return server;
    }

    /** @return 触发玩家 */
    public ServerPlayer getPlayer() {
        return player;
    }

    /** @return 变更后是否处于时停 */
    public boolean isActive() {
        return active;
    }

    /** @return 变更前是否处于时停 */
    public boolean wasActive() {
        return previousActive;
    }

    /** @return 时停触发者 UUID；未激活时为 null */
    @Nullable
    public java.util.UUID getOwnerUuid() {
        return active ? player.getUUID() : null;
    }
}
