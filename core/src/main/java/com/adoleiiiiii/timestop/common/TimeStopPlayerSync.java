package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.network.TimeStopVisualPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 玩家进服时补发当前时停视觉状态，避免联机 desync。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID)
public final class TimeStopPlayerSync {
    private TimeStopPlayerSync() {
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!TimeStopManager.isActive()) {
            return;
        }
        ServerPlayer owner = TimeStopManager.getOwner(player.getServer());
        int ownerId = owner != null ? owner.getId() : TimeStopManager.getOwnerEntityId();
        PacketDistributor.sendToPlayer(player, new TimeStopVisualPayload(true, ownerId, TimeStopManager.getOwnerUuid()));
    }
}
