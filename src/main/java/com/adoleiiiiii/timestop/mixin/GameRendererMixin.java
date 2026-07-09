package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停期间锁定配置的 post shader；玩家第一人称仍使用独立计时器插值。
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Shadow
    @Nullable
    PostChain postEffect;
    @Shadow
    @Final
    Minecraft minecraft;

    @ModifyVariable(method = "loadEffect", at = @At("HEAD"), argsOnly = true)
    private ResourceLocation forceConfiguredShader(ResourceLocation location) {
        if (Time.isClientActive() && TimeStopClientConfig.isPostEffectEnabled()) {
            return TimeStopClientConfig.resolvePostEffect();
        }
        return location;
    }

    @Inject(method = "loadEffect", at = @At("HEAD"), cancellable = true)
    private void cancelDuplicateActiveEffect(CallbackInfo ci) {
        if (postEffect != null && Time.isActivePostEffectName(postEffect.getName())) {
            ci.cancel();
        }
    }

    @Inject(method = "shutdownEffect", at = @At("HEAD"), cancellable = true)
    private void onShutdownEffect(CallbackInfo ci) {
        if (Time.isClientActive() && Time.getActivePostEffect() != null && postEffect != null
                && Time.isActivePostEffectName(postEffect.getName())) {
            ci.cancel();
        }
    }

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void onCheckEntityPostEffect(Entity entity, CallbackInfo ci) {
        if (Time.isClientActive() && Time.getActivePostEffect() != null && postEffect != null
                && Time.isActivePostEffectName(postEffect.getName())) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "renderItemInHand", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float useTimeStopHandPartialTick(float partialTick) {
        return Time.isClientActive()
                ? Time.timer.getGameTimeDeltaPartialTick(true)
                : partialTick;
    }
}
