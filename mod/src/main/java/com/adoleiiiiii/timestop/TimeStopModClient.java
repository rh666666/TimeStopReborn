package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.render.entity.KnifeRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

/**
 * 默认内容客户端注册。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID, value = Dist.CLIENT)
public final class TimeStopModClient {
    private TimeStopModClient() {
    }

    @SubscribeEvent
    static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (!TimeStopFeatureGate.registerDefaultContent() || !EntityRegister.FLYING_SWORD.isBound()) {
            return;
        }
        event.registerEntityRenderer(EntityRegister.FLYING_SWORD.get(), KnifeRenderer::new);
    }
}
