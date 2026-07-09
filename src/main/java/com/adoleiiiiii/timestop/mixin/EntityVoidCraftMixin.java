package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import com.adoleiiiiii.timestop.common.NetherStarVoidCraftHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@code onBelowWorld} 注入点，拦截下界之星避免原版销毁。
 */
@Mixin(Entity.class)
public abstract class EntityVoidCraftMixin {
    @Inject(method = "onBelowWorld", at = @At("HEAD"), cancellable = true)
    private void timestopreborn$netherStarVoidCraft(CallbackInfo ci) {
        if (!TimeStopFeatureGate.registerDefaultContent()) {
            return;
        }
        Entity self = (Entity) (Object) this;
        if (self.level().isClientSide() || !(self instanceof ItemEntity itemEntity)) {
            return;
        }
        if (!itemEntity.getItem().is(Items.NETHER_STAR)) {
            return;
        }
        NetherStarVoidCraftHandler.onVoidEntry((ServerLevel) self.level(), itemEntity);
        ci.cancel();
    }
}
