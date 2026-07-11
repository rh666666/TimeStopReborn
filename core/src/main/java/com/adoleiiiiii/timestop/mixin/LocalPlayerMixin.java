package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停且非触发者时，阻止本地玩家 aiStep 推进位移与发包。
 */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    private void timestopreborn$blockNonOwnerAiStep(CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (Time.isClientGameplaySuspended(mc) && !Time.canLocalPlayerAct()) {
            ci.cancel();
        }
    }
}
