package com.adoleiiiiii.timestop.data;

import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * 注册 Datagen Provider。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID)
public final class DataGenerators {
    private DataGenerators() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        if (!TimeStopFeatureGate.registerDefaultContent()) {
            return;
        }
        event.getGenerator().addProvider(event.includeClient(),
                new ModItemModelProvider(event.getGenerator().getPackOutput(), event.getExistingFileHelper()));
    }
}
