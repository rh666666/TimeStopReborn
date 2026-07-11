package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.common.TimeStopManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停且仅触发者可动时，阻止非触发者在服务端通过 travel 位移。
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {
    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void timestopreborn$blockNonOwnerTravel(Vec3 travelVector, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;
        if (!TimeStopManager.canPlayerAct(self)) {
            ci.cancel();
        }
    }
}
