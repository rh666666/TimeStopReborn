package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.ClockItem;
import com.adoleiiiiii.timestop.Time;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.TimerQuery;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

/**
 * 时停期间替换客户端 tick 与 runTick 后半段逻辑。
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public static int fps;
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
    @Nullable
    public IntegratedServer singleplayerServer;
    @Shadow
    public ParticleEngine particleEngine;
    @Shadow
    public Window window;
    @Shadow
    public int frames;
    @Shadow
    public long lastNanoTime;
    @Shadow
    public Options options;
    @Shadow
    public MetricsRecorder metricsRecorder;
    @Shadow
    public double gpuUtilization;
    @Shadow
    @Nullable
    public TimerQuery.FrameProfile currentFrameProfile;
    @Shadow
    public long savedCpuDuration;
    @Shadow
    public long lastTime;
    @Shadow
    public String fpsString;

    @Shadow
    public abstract void setScreen(@Nullable Screen screen);

    @Shadow
    protected abstract void handleKeybinds();

    @Shadow
    public abstract boolean hasSingleplayerServer();

    @Shadow
    protected abstract int getFramerateLimit();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (Time.isClientActive()) {
            pause = true;
            ClockItem.stoppingTime++;
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
            if (this.overlay == null && this.screen == null) {
                this.handleKeybinds();
                if (this.missTime > 0) {
                    --this.missTime;
                }
            }
            if (level != null) {
                level.tickingEntities.forEach(entity -> {
                    if (!entity.isRemoved() && !entity.isPassenger()) {
                        if (entity instanceof Player && entity != player) {
                            level.guardEntityTick(level::tickNonPassenger, entity);
                        }
                        if (entity.tickCount < 1) {
                            level.guardEntityTick(level::tickNonPassenger, entity);
                        }
                    }
                });
            }
            ci.cancel();
        } else {
            ClockItem.stoppingTime = 0;
        }
    }

    /**
     * 渲染前同步原版计时器冻结态，并推进玩家独立 tick。
     */
    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void prepareTimeStopRender(boolean renderLevel, CallbackInfo ci) {
        if (Time.isClientActive()) {
            this.timer.updateFrozenState(true);
        }
        for (int i = 0; i < Time.timer.advanceTime(Util.getMillis(), true); i++) {
            if (level != null && player != null && Time.isClientActive()) {
                level.guardEntityTick(level::tickNonPassenger, player);
                this.gui.tick(this.pause);
                gameRenderer.itemInHandRenderer.tick();
                this.gameRenderer.tick();
            }
        }
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/Window;setErrorSection(Ljava/lang/String;)V", ordinal = 2),
            cancellable = true)
    private void onPostRender(boolean renderLevel, CallbackInfo ci) {
        if (Time.isClientActive()) {
            this.pause = true;
            this.timer.updatePauseState(true);
            this.timer.updateFrozenState(true);
            this.window.setErrorSection("Post render");
            ++frames;
            boolean profileGpu;
            if (!((Minecraft) (Object) this).getDebugOverlay().showDebugScreen() && !this.metricsRecorder.isRecording()) {
                profileGpu = false;
                this.gpuUtilization = 0.0D;
            } else {
                profileGpu = this.currentFrameProfile == null || this.currentFrameProfile.isDone();
                if (profileGpu) {
                    TimerQuery.getInstance().ifPresent(TimerQuery::beginProfile);
                }
            }
            long now = Util.getNanos();
            long frameDuration = now - lastNanoTime;
            if (profileGpu) {
                this.savedCpuDuration = frameDuration;
            }
            ((Minecraft) (Object) this).getDebugOverlay().logFrameDuration(frameDuration);
            this.lastNanoTime = now;
            this.profiler.push("fpsUpdate");
            if (this.currentFrameProfile != null && this.currentFrameProfile.isDone()) {
                this.gpuUtilization = (double) this.currentFrameProfile.get() * 100.0D / (double) this.savedCpuDuration;
            }
            while (Util.getMillis() >= this.lastTime + 1000L) {
                String gpuText;
                if (this.gpuUtilization > 0.0D) {
                    gpuText = " GPU: " + (this.gpuUtilization > 100.0D
                            ? ChatFormatting.RED + "100%"
                            : Math.round(this.gpuUtilization) + "%");
                } else {
                    gpuText = "";
                }
                int limit = this.getFramerateLimit();
                fps = this.frames;
                this.fpsString = String.format(Locale.ROOT, "%d fps T: %s%s%s%s B: %d%s", fps,
                        limit == 260 ? "inf" : limit,
                        this.options.enableVsync().get() ? " vsync" : "",
                        this.options.graphicsMode().get(),
                        this.options.cloudStatus().get() == CloudStatus.OFF ? ""
                                : (this.options.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"),
                        this.options.biomeBlendRadius().get(), gpuText);
                this.lastTime += 1000L;
                this.frames = 0;
            }
            this.profiler.pop();
            ci.cancel();
        }
    }
}
