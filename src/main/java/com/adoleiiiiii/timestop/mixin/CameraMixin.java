package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * 时停期间本地玩家相机使用独立计时器的正常插值分支。
 */
@Mixin(Camera.class)
public class CameraMixin {
    @ModifyVariable(method = "setup", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float useTimeStopPartialTick(float partialTick) {
        return Time.isClientActive()
                ? Time.timer.getGameTimeDeltaPartialTick(true)
                : partialTick;
    }
}
