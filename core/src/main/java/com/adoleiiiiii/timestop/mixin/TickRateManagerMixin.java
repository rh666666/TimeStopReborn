package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 时停截断客户端 tick 后，立即同步 {@code runGameElements}，使 {@link #isEntityFrozen} 生效。
 */
@Mixin(TickRateManager.class)
public abstract class TickRateManagerMixin {
    @Shadow
    protected boolean runGameElements;
    @Shadow
    protected boolean isFrozen;
    @Shadow
    protected int frozenTicksToRun;

    /**
     * 镜像 {@link TickRateManager#tick()} 内对 {@code runGameElements} 的赋值逻辑。
     */
    private void timestopreborn$syncRunGameElements() {
        this.runGameElements = !this.isFrozen || this.frozenTicksToRun > 0;
    }

    @Inject(method = "setFrozen", at = @At("TAIL"))
    private void onSetFrozen(boolean frozen, CallbackInfo ci) {
        this.timestopreborn$syncRunGameElements();
    }

    @Inject(method = "setFrozenTicksToRun", at = @At("TAIL"))
    private void onSetFrozenTicksToRun(int frozenTicksToRun, CallbackInfo ci) {
        this.timestopreborn$syncRunGameElements();
    }

    /**
     * 进服加载地形期间忽略服务端同步的 frozen 状态，保证 levelRenderer 与连接 tick 可推进。
     */
    @Inject(method = "runsNormally", at = @At("HEAD"), cancellable = true)
    private void allowTicksDuringWorldJoin(CallbackInfoReturnable<Boolean> cir) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (Time.isClientActive() && Time.isClientWorldJoinInProgress(mc)) {
            cir.setReturnValue(true);
        }
    }

    /**
     * 兜底 S2C 包与视觉包到达顺序差异，确保时停期间非玩家实体被判定为冻结。
     */
    @Inject(method = "isEntityFrozen", at = @At("HEAD"), cancellable = true)
    private void overrideEntityFrozenDuringTimeStop(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = Minecraft.getInstance();
        if (Time.isClientWorldJoinInProgress(mc)) {
            return;
        }
        if (Time.isClientActive()
                && Time.isClientGameplaySuspended(mc)
                && !(entity instanceof Player)
                && entity.countPlayerPassengers() <= 0) {
            cir.setReturnValue(true);
        }
    }
}
