package com.adoleiiiiii.timestop.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * 注册时停相关 NeoForge 网络包。
 */
public final class TimeStopPackets {
    private TimeStopPackets() {
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                TimeStopVisualPayload.TYPE,
                TimeStopVisualPayload.STREAM_CODEC,
                TimeStopVisualPayload::handle
        );
    }
}
