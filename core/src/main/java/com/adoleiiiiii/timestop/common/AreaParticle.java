package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.render.RendererUtils;
import com.adoleiiiiii.timestop.render.shader.CullWrappedRenderLayer;
import com.adoleiiiiii.timestop.render.shader.GlowRenderLayer;
import com.adoleiiiiii.timestop.render.shader.MegaRenderType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时停范围球体粒子，使用自定义 NO_RENDER 路径绘制。
 */
public class AreaParticle extends Particle {
    private static final int SPHERE_GRADATION = 12;
    private static final Map<ResourceLocation, GlowRenderLayer> GLOW_LAYER_CACHE = new ConcurrentHashMap<>();

    private int maxLife = 100;
    public boolean growing = true;
    public String texturePath;
    public float[] rgba = new float[4];
    public float sz;
    public double slow;
    public double grow = 0.4D;

    public AreaParticle(ClientLevel level, double x, double y, double z, float scale,
                        double vx, double vy, double vz, String texturePath,
                        float sz, float r, float g, float b, float a, boolean shaders, double grow) {
        super(level, x, y, z, vx, vy, vz);
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;
        this.alpha = 0.4F;
        this.lifetime = 100;
        this.maxLife = lifetime;
        this.rgba[0] = r;
        this.rgba[1] = g;
        this.rgba[2] = b;
        this.rgba[3] = a;
        this.texturePath = texturePath;
        this.sz = sz;
        this.slow = 0.6D;
        this.grow = grow;
    }

    private static GlowRenderLayer getGlowLayer(ResourceLocation texture) {
        return GLOW_LAYER_CACHE.computeIfAbsent(texture, tex -> new GlowRenderLayer(
                new CullWrappedRenderLayer(MegaRenderType.createSphereRenderType(tex, tex.hashCode()))));
    }

    /**
     * 在 AFTER_PARTICLES 阶段手动绘制粒子球体。
     */
    public static void render(AreaParticle particle, double camX, double camY, double camZ,
                              PoseStack matrix, float partialTicks) {
        double x = particle.xo + (particle.x - particle.xo) * partialTicks;
        double y = particle.yo + (particle.y - particle.yo) * partialTicks;
        double z = particle.zo + (particle.z - particle.zo) * partialTicks;
        matrix.pushPose();
        matrix.translate(x - camX, y - camY, z - camZ);
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        if (cameraEntity != null) {
            float r = particle.rgba[0];
            float g = particle.rgba[1];
            float b = particle.rgba[2];
            float a = particle.rgba[3];
            MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
            ResourceLocation texture = ResourceLocation.parse(particle.texturePath);
            GlowRenderLayer glowLayer = getGlowLayer(texture);
            double size = particle.growing
                    ? Math.max(0.0D, particle.sz - particle.grow + particle.grow * partialTicks)
                    : Math.max(0.0D, particle.sz + particle.grow - particle.grow * partialTicks);
            RendererUtils.renderSphere(matrix, buffer, (float) size, SPHERE_GRADATION, 240, 240, r, g, b, a, glowLayer);
            buffer.endBatch(glowLayer);
        }
        matrix.popPose();
    }

    @Override
    public void setLifetime(int lifetime) {
        super.setLifetime(lifetime);
        maxLife = lifetime;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            remove();
        }
        if (this.rgba[3] <= 0.0F) {
            remove();
        }
        this.xd *= this.slow;
        this.yd *= this.slow;
        this.zd *= this.slow;
        if (age > maxLife / 2 + 10) {
            growing = false;
        }
        if (growing) {
            this.sz = (float) (this.sz + this.grow);
        } else {
            this.sz = (float) (this.sz - this.grow);
        }
        this.oRoll = this.roll;
        setPos(getPos().x, getPos().y, getPos().z);
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
    }

    public Vec3 getPos() {
        return new Vec3(x, y, z);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }
}
