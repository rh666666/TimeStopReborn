package com.adoleiiiiii.timestop.mixin.accessor;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * 暴露网络位置/头部插值步数，供时停快照清零残留 lerp。
 */
@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {
    /** @param steps 剩余位置插值步数 */
    @Accessor("lerpSteps")
    void setLerpSteps(int steps);

    /** @param steps 剩余头部旋转插值步数 */
    @Accessor("lerpHeadSteps")
    void setLerpHeadSteps(int steps);
}
