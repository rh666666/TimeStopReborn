package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 时停期间非玩家受击后在客户端对齐插值基准，避免击退同步引发抖动。
 */
@Mixin(LivingEntity.class)
public abstract class LivingEntityClientMixin {
    @Inject(method = "hurt", at = @At("RETURN"))
    private void alignInterpolationAfterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Time.isClientActive() || !Boolean.TRUE.equals(cir.getReturnValue())) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (source.getEntity() instanceof Player && !(self instanceof Player)) {
            self.setOldPosAndRot();
        }
    }
}
