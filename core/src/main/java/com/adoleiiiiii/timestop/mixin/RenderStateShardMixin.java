package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停期间附魔光效使用独立 millis 驱动。
 */
@Mixin(RenderStateShard.class)
public class RenderStateShardMixin {
    @Inject(method = "setupGlintTexturing", at = @At("HEAD"), cancellable = true)
    private static void onSetupGlintTexturing(float scale, CallbackInfo ci) {
        if (Time.isClientActive()) {
            long elapsed = (long) ((double) Time.millis * Minecraft.getInstance().options.glintSpeed().get() * 8.0D);
            float u = (float) (elapsed % 110000L) / 110000.0F;
            float v = (float) (elapsed % 30000L) / 30000.0F;
            Matrix4f matrix = new Matrix4f().translation(-u, v, 0.0F);
            matrix.rotateZ(0.17453292F).scale(scale);
            RenderSystem.setTextureMatrix(matrix);
            ci.cancel();
        }
    }
}
