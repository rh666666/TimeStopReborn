package com.adoleiiiiii.timestop.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

import java.util.Objects;
import java.util.Optional;

/**
 * 在 delegate RenderType 上叠加混合与深度状态。
 */
public class GlowRenderLayer extends RenderType {
    private final RenderType delegate;

    public GlowRenderLayer(RenderType delegate) {
        super("timestop_glow_" + delegate, delegate.format(), delegate.mode(), delegate.bufferSize(),
                true, delegate.isOutline(), () -> {
                    delegate.setupRenderState();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    GL11.glDepthFunc(513);
                    GL11.glDepthMask(false);
                }, () -> {
                });
        this.delegate = delegate;
    }

    @Override
    public Optional<RenderType> outline() {
        return delegate.outline();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GlowRenderLayer glow && delegate.equals(glow.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
