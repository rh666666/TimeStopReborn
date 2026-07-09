package com.adoleiiiiii.timestop.network;

import com.adoleiiiiii.timestop.Time;
import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * S2C：同步时停视觉状态（shader、音效、粒子）至所有客户端。
 *
 * @param active        是否进入时停
 * @param ownerEntityId 触发者在服务端上的实体 id
 */
public record TimeStopVisualPayload(boolean active, int ownerEntityId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TimeStopVisualPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "time_stop_visual"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TimeStopVisualPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, TimeStopVisualPayload::active,
                    ByteBufCodecs.VAR_INT, TimeStopVisualPayload::ownerEntityId,
                    TimeStopVisualPayload::new
            );

    public static void handle(TimeStopVisualPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> Time.applyClientVisuals(payload.active(), payload.ownerEntityId()));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
