package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 时停期间非玩家实体视角插值直接返回当前朝向，消除旋转 lerping 抖动。
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getViewXRot", at = @At("HEAD"), cancellable = true)
    private void freezeViewXRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        if (Time.isClientActive() && !(self instanceof Player)) {
            cir.setReturnValue(self.getXRot());
        }
    }

    @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
    private void freezeViewYRot(float partialTick, CallbackInfoReturnable<Float> cir) {
        Entity self = (Entity) (Object) this;
        if (Time.isClientActive() && !(self instanceof Player)) {
            cir.setReturnValue(self.getYRot());
        }
    }
}
