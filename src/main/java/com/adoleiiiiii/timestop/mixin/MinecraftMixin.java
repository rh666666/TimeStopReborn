package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停期间替换客户端 tick 与 runTick 后半段逻辑。
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public int rightClickDelay;
    @Shadow
    public ProfilerFiller profiler;
    @Shadow
    public Gui gui;
    @Shadow
    public volatile boolean pause;
    @Shadow
    @Nullable
    public Screen screen;
    @Shadow
    @Nullable
    public LocalPlayer player;
    @Shadow
    @Nullable
    public ClientLevel level;
    @Shadow
    public int missTime;
    @Shadow
    @Nullable
    public Overlay overlay;
    @Shadow
    public GameRenderer gameRenderer;
    @Shadow
    @Final
    private DeltaTracker.Timer timer;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow
    protected abstract void handleKeybinds();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (Time.isClientActive()) {
            pause = true;
            if (this.rightClickDelay > 0) {
                --this.rightClickDelay;
            }
            this.profiler.push("gui");
            this.profiler.pop();
            if (this.screen == null && this.player != null) {
                if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
                    this.setScreen(null);
                } else if (this.player.isSleeping() && this.level != null) {
                    this.setScreen(new InBedChatScreen());
                }
            } else if (this.screen instanceof InBedChatScreen inBedChatScreen) {
                if (this.player != null && !this.player.isSleeping()) {
                    inBedChatScreen.onPlayerWokeUp();
                }
            }
            if (this.screen != null) {
                missTime = 10000;
            }
            if (this.screen != null) {
                Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
            }
            if (this.overlay == null && this.screen == null && Time.canLocalPlayerAct()) {
                this.handleKeybinds();
                if (this.missTime > 0) {
                    --this.missTime;
                }
            }
            if (level != null) {
                for (Player other : level.players()) {
                    if (other != player && !other.isRemoved() && !other.isPassenger()) {
                        level.guardEntityTick(level::tickNonPassenger, other);
                    }
                }
            }
            ci.cancel();
        }
    }

    /** 渲染前同步原版计时器冻结态，并推进玩家独立 tick。 */
    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void prepareTimeStopRender(boolean renderLevel, CallbackInfo ci) {
        if (!Time.isClientActive() || !Time.canLocalPlayerAct()) {
            return;
        }
        this.timer.updateFrozenState(true);
        for (int i = 0; i < Time.timer.advanceTime(Util.getMillis(), true); i++) {
            if (level != null && player != null) {
                level.guardEntityTick(level::tickNonPassenger, player);
                this.gui.tick(Time.isClientActive() && Time.canLocalPlayerAct() ? false : this.pause);
                gameRenderer.itemInHandRenderer.tick();
                this.gameRenderer.tick();
            }
        }
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/DeltaTracker$Timer;updatePauseState(Z)V"))
    private void timestopreborn$forcePauseFlag(CallbackInfo ci) {
        if (Time.isClientActive()) {
            this.pause = true;
        }
    }

    @ModifyVariable(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/DeltaTracker$Timer;updateFrozenState(Z)V"), ordinal = 0)
    private boolean timestopreborn$forceFrozenState(boolean frozen) {
        if (Time.isClientActive() && Time.getActivePostEffect() != null) {
            return true;
        }
        return frozen;
    }
}
