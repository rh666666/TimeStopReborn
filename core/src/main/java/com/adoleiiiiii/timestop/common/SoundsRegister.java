package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 时停核心音效事件注册。
 */
public final class SoundsRegister {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TimeStopReborn.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STOP = SOUNDS.register("stop",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "stop")));

    private SoundsRegister() {
    }
}
