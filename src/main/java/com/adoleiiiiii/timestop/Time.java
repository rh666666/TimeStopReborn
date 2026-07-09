package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.common.AreaParticle;
import com.adoleiiiiii.timestop.common.ModifyShader;
import com.adoleiiiiii.timestop.common.SoundsRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
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

/**
 * 客户端时停镜像状态与独立渲染计时器。
 */
public final class Time {
    public static long millis = 0L;
    public static final DeltaTracker.Timer timer = new DeltaTracker.Timer(20.0F, 0L, t -> t);

    private static volatile boolean clientActive = false;
    @Nullable
    private static volatile ResourceLocation activePostEffect = null;

    private Time() {
    }

    /**
     * 由 S2C 包驱动，应用时停视觉与插值冻结。
     *
     * @param ownerEntityId 触发者实体 id；-1 表示未知
     */
    public static void applyClientVisuals(boolean active, int ownerEntityId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        boolean wasActive = clientActive;
        clientActive = active;
        if (active && !wasActive) {
            Entity owner = mc.level.getEntity(ownerEntityId);
            if (owner instanceof Player player) {
                player.playSound(SoundsRegister.STOP.get(), 1f, 1f);
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
        if (!TimeStopClientConfig.isPostEffectEnabled()) {
            activePostEffect = null;
            return;
        }
        ResourceLocation effect = TimeStopClientConfig.resolvePostEffect();
        mc.gameRenderer.loadEffect(effect);
        PostChain chain = mc.gameRenderer.currentEffect();
        if (chain != null) {
            activePostEffect = effect;
        } else {
            activePostEffect = null;
            TimeStopReborn.LOGGER.warn("Failed to load time stop post effect: {}", effect);
        }
    }

    private static void shutdownActivePostEffect(Minecraft mc) {
        if (activePostEffect != null) {
            mc.gameRenderer.shutdownEffect();
            activePostEffect = null;
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
        return effectName.endsWith(active.getPath());
    }
}
