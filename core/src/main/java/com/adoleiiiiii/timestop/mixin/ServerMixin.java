package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.common.TimeStopManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停期间冻结服务端昼夜与方块随机 tick。
 */
@Mixin(ServerLevel.class)
public abstract class ServerMixin {
    @Inject(method = "tickTime", at = @At("HEAD"), cancellable = true)
    private void onTickTime(CallbackInfo ci) {
        if (TimeStopManager.isActive()) {
            ci.cancel();
        }
    }

    @Inject(method = "tickBlock", at = @At("HEAD"), cancellable = true)
    private void onTickBlock(BlockPos pos, Block block, CallbackInfo ci) {
        if (TimeStopManager.isActive()) {
            ci.cancel();
        }
    }
}
