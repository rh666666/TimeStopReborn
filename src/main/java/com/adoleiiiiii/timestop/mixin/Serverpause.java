package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

/**
 * 时停期间阻止集成服务器进入暂停状态。
 */
@Mixin(IntegratedServer.class)
public class Serverpause {
    @Shadow
    private boolean paused;

    @Inject(method = "tickServer", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/server/IntegratedServer;getProfiler()Lnet/minecraft/util/profiling/ProfilerFiller;"))
    private void onTickServer(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        if (Time.isClientActive()) {
            paused = false;
        }
    }
}
