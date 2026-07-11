package com.adoleiiiiii.timestop.render.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.RenderType;

import java.util.Objects;
import java.util.Optional;

/**
 * 包装 delegate RenderType 并在结束时恢复剔除状态。
 */
public class CullWrappedRenderLayer extends RenderType {
    private final RenderType delegate;

    public CullWrappedRenderLayer(RenderType delegate) {
        super("timestop_cull_" + delegate, delegate.format(), delegate.mode(), delegate.bufferSize(),
                true, delegate.isOutline(), () -> {
                    delegate.setupRenderState();
                    RenderSystem.disableBlend();
                }, () -> {
                    RenderSystem.enableCull();
                    delegate.clearRenderState();
                });
        this.delegate = delegate;
    }

    @Override
    public Optional<RenderType> outline() {
        return delegate.outline();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CullWrappedRenderLayer wrapped && delegate.equals(wrapped.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
