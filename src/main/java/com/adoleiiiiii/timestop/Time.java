package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import com.adoleiiiiii.timestop.common.AreaParticle;
import com.adoleiiiiii.timestop.common.ModifyShader;
import com.adoleiiiiii.timestop.common.SoundsRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.config.TimeStopCommonConfig;
import com.adoleiiiiii.timestop.render.RendererUtils;
import com.adoleiiiiii.timestop.render.ShaderGetter;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * 客户端时停镜像状态与独立渲染计时器。
 */
public final class Time {
    public static long millis = 0L;
    public static final DeltaTracker.Timer timer = new DeltaTracker.Timer(20.0F, 0L, t -> t);

    private static volatile boolean clientActive = false;
    @Nullable
    private static volatile UUID clientOwnerUuid = null;
    @Nullable
    private static volatile ResourceLocation activePostEffect = null;
    private static volatile boolean animationOnlyPostEffect = false;

    private static volatile boolean pendingVisualApply = false;
    private static volatile boolean pendingActive = false;
    private static volatile boolean pendingWasActive = false;
    private static volatile int pendingOwnerEntityId = -1;

    private Time() {
    }

    /**
     * 由 S2C 包驱动，应用时停视觉与插值冻结。
     *
     * @param ownerEntityId 触发者实体 id；-1 表示未知
     * @param ownerUuid     触发者 UUID；未激活时为 null
     */
    public static void applyClientVisuals(boolean active, int ownerEntityId, @Nullable UUID ownerUuid) {
        boolean wasActive = clientActive;
        clientActive = active;
        clientOwnerUuid = active ? ownerUuid : null;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            pendingVisualApply = true;
            pendingActive = active;
            pendingWasActive = wasActive;
            pendingOwnerEntityId = ownerEntityId;
            return;
        }
        applyVisualTransition(mc, wasActive, active, ownerEntityId);
    }

    /** 维度就绪后应用挂起的视觉变更。 */
    public static void applyPendingVisuals() {
        if (!pendingVisualApply) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        pendingVisualApply = false;
        applyVisualTransition(mc, pendingWasActive, pendingActive, pendingOwnerEntityId);
    }

    private static void applyVisualTransition(Minecraft mc, boolean wasActive, boolean active, int ownerEntityId) {
        if (active && !wasActive) {
            Entity owner = mc.level.getEntity(ownerEntityId);
            if (owner instanceof Player player) {
                if (TimeStopFeatureGate.registerDefaultContent()) {
                    player.playSound(SoundsRegister.STOP.get(), 1f, 1f);
                }
                spawnAreaParticle(player);
            }
            ShaderGetter.invalidateTimeUniformCache();
            ModifyShader.resetStartupAnimation();
            loadConfiguredPostEffect(mc);
            snapshotFrozenEntities(mc);
        } else if (!active && wasActive) {
            shutdownActivePostEffect(mc);
        }
        mc.particleEngine.tick();
    }

    /**
     * 配置重载时刷新当前 post 滤镜；仅在时停激活中调用。
     */
    public static void reloadPostEffectFromConfig() {
        if (!clientActive) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        shutdownActivePostEffect(mc);
        ShaderGetter.invalidateTimeUniformCache();
        loadConfiguredPostEffect(mc);
    }

    private static void loadConfiguredPostEffect(Minecraft mc) {
        ResourceLocation effect = TimeStopClientConfig.resolveActivePostEffect();
        if (effect == null) {
            activePostEffect = null;
            animationOnlyPostEffect = false;
            return;
        }
        animationOnlyPostEffect = !TimeStopClientConfig.isSustainedPostEffect();
        mc.gameRenderer.loadEffect(effect);
        PostChain chain = mc.gameRenderer.currentEffect();
        if (chain != null) {
            activePostEffect = effect;
        } else {
            activePostEffect = null;
            animationOnlyPostEffect = false;
            TimeStopReborn.LOGGER.warn("Failed to load time stop post effect: {}", effect);
        }
    }

    /**
     * 启动动画专用 post chain 播放完毕后卸载，避免无滤镜模式下 GUI 渲染异常。
     *
     * @param mc 当前客户端
     */
    public static void finishAnimationOnlyPostEffect(Minecraft mc) {
        if (!animationOnlyPostEffect) {
            return;
        }
        animationOnlyPostEffect = false;
        shutdownActivePostEffect(mc);
        ModifyShader.resetStartupAnimation();
    }

    /** @return 当前是否处于仅播放启动动画、不保留持续滤镜的阶段 */
    public static boolean isAnimationOnlyPostEffect() {
        return animationOnlyPostEffect;
    }

    private static void shutdownActivePostEffect(Minecraft mc) {
        if (activePostEffect != null) {
            mc.gameRenderer.shutdownEffect();
            activePostEffect = null;
            animationOnlyPostEffect = false;
            ShaderGetter.invalidateTimeUniformCache();
        }
    }

    private static void spawnAreaParticle(Player player) {
        AreaParticle particle = new AreaParticle(Minecraft.getInstance().level,
                player.getX(), player.getY(), player.getZ(),
                0.4F, 0.0D, 0.0D, 0.0D, RendererUtils.BEAM_TEXTURE.toString(),
                0.62F, 0.3F, 0.3F, 0.3F, 0.4F, false, 1.4d) {
            @Override
            public Vec3 getPos() {
                return player.position();
            }
        };
        particle.setLifetime(80);
        Minecraft.getInstance().particleEngine.add(particle);
    }

    /** 进入时停时对非玩家实体对齐插值基准，消除 xo/x 残留导致的渲染抖动。 */
    public static void snapshotFrozenEntities(Minecraft mc) {
        if (mc.level == null) {
            return;
        }
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof Player)) {
                entity.setOldPosAndRot();
            }
        }
    }

    /** @return 客户端是否处于时停视觉状态 */
    public static boolean isClientActive() {
        return clientActive;
    }

    /**
     * 时停期间本地玩家是否允许行动（移动、输入、独立计时器）。
     * 未开启 onlyOwnerCanMove 时，时停中所有客户端玩家均可行动。
     */
    public static boolean canLocalPlayerAct() {
        if (!clientActive) {
            return true;
        }
        if (!TimeStopCommonConfig.isOnlyOwnerCanMove()) {
            return true;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || clientOwnerUuid == null) {
            return false;
        }
        return mc.player.getUUID().equals(clientOwnerUuid);
    }

    /** @return 当前由 mod 加载的 post effect；未加载或已关闭时为 null */
    @Nullable
    public static ResourceLocation getActivePostEffect() {
        return activePostEffect;
    }

    /**
     * 判断 {@link PostChain#getName()} 是否对应当前 mod 锁定的滤镜。
     */
    public static boolean isActivePostEffectName(String effectName) {
        ResourceLocation active = activePostEffect;
        if (active == null || effectName == null) {
            return false;
        }
        String normalized = effectName.replace('\\', '/');
        String activeId = active.toString();
        String activePath = active.getPath();
        return normalized.equals(activeId)
                || normalized.equals(activePath)
                || normalized.endsWith('/' + active.getNamespace() + '/' + activePath);
    }
}
