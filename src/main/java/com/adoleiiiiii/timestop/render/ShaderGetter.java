package com.adoleiiiiii.timestop.render;

import com.mojang.blaze3d.shaders.Uniform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;

/**
 * 读写 GameRenderer 当前 post shader 的 uniform。
 */
public final class ShaderGetter {
    private static final Minecraft MC = Minecraft.getInstance();
    private static Uniform cachedTimeUniform;

    private ShaderGetter() {
    }

    /** 清除 time uniform 缓存；post effect 切换后须调用。 */
    public static void invalidateTimeUniformCache() {
        cachedTimeUniform = null;
    }

    public static void updateUniformPost(String name, float value) {
        if ("time".equals(name)) {
            Uniform uniform = getOrCacheTimeUniform();
            if (uniform != null) {
                uniform.set(value);
                return;
            }
        }
        PostChain effect = MC.gameRenderer.currentEffect();
        if (effect == null) {
            return;
        }
        for (PostPass pass : effect.passes) {
            Uniform uniform = pass.getEffect().getUniform(name);
            if (uniform != null) {
                uniform.set(value);
            }
        }
    }

    private static Uniform getOrCacheTimeUniform() {
        if (cachedTimeUniform != null) {
            return cachedTimeUniform;
        }
        PostChain effect = MC.gameRenderer.currentEffect();
        if (effect == null) {
            return null;
        }
        for (PostPass pass : effect.passes) {
            Uniform uniform = pass.getEffect().getUniform("time");
            if (uniform != null) {
                cachedTimeUniform = uniform;
                return uniform;
            }
        }
        return null;
    }

    /** @return 当前 post chain 是否包含名为 time 的 uniform */
    public static boolean hasTimeUniform() {
        return getOrCacheTimeUniform() != null;
    }
}
