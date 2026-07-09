package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 模组音效事件注册。
 */
public final class SoundsRegister {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(Registries.SOUND_EVENT, TimeStopReborn.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> STOP = SOUNDS.register("stop",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "stop")));

    public static final DeferredHolder<SoundEvent, SoundEvent> THROW = SOUNDS.register("knife_throw",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "knife_throw")));

    public static final DeferredHolder<SoundEvent, SoundEvent> KNIFE_HIT = SOUNDS.register("knife_hit",
            () -> SoundEvent.createVariableRangeEvent(
                    ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "knife_hit")));

    private SoundsRegister() {
    }
}
