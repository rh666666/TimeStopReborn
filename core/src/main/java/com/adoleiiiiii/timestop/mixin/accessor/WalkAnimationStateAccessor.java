package com.adoleiiiiii.timestop.mixin.accessor;

import net.minecraft.world.entity.WalkAnimationState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露行走动画插值基准，供时停快照对齐 {@code speedOld}。
 */
@Mixin(WalkAnimationState.class)
public interface WalkAnimationStateAccessor {
    /**
     * @return 上一帧行走速度，用于 {@link WalkAnimationState#speed(float)} 插值
     */
    @Accessor("speedOld")
    void setSpeedOld(float speedOld);
}
