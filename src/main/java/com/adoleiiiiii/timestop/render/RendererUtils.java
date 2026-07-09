package com.adoleiiiiii.timestop.render;

import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.common.AreaParticle;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.Map;
import java.util.Queue;

/**
 * 自定义粒子与球体网格绘制工具。
 */
@EventBusSubscriber(modid = TimeStopReborn.MODID, value = Dist.CLIENT)
public final class RendererUtils {
    public static final ResourceLocation BEAM_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "textures/item/white.png");

    private static Map<ParticleRenderType, Queue<Particle>> particleQueues;

    private RendererUtils() {
    }

    public static void renderSphere(PoseStack matrix, MultiBufferSource buffer, float radius, int gradation,
                                    int lightX, int lightY, float r, float g, float b, float a,
                                    RenderType type, float percentage) {
        float pi = 3.141592653589792F;
        VertexConsumer consumer = buffer.getBuffer(type);
        var pose = matrix.last().pose();
        for (float alpha = 0.0F; alpha < pi; alpha += pi / gradation) {
            for (float beta = 0.0F; beta < pi * 2 * percentage; beta += pi / gradation) {
                float x = (float) (radius * Math.cos(beta) * Math.sin(alpha));
                float y = (float) (radius * Math.sin(beta) * Math.sin(alpha));
                float z = (float) (radius * Math.cos(alpha));
                consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightX, lightY);
                float sin = (float) Math.sin(alpha + pi / gradation);
                x = (float) (radius * Math.cos(beta) * sin);
                y = (float) (radius * Math.sin(beta) * sin);
                z = (float) (radius * Math.cos(alpha + pi / gradation));
                consumer.addVertex(pose, x, y, z).setColor(r, g, b, a).setUv(0.0F, 1.0F).setUv2(lightX, lightY);
            }
        }
    }

    public static void renderSphere(PoseStack matrix, MultiBufferSource buffer, float radius, int gradation,
                                    int lightX, int lightY, float r, float g, float b, float a, RenderType type) {
        renderSphere(matrix, buffer, radius, gradation, lightX, lightY, r, g, b, a, type, 1.0F);
    }

    public static void particleRenders(Entity cameraEntity, PoseStack matrix, float partialTicks) {
        ParticleEngine engine = Minecraft.getInstance().particleEngine;
        if (particleQueues == null) {
            particleQueues = engine.particles;
        }
        Queue<Particle> queue = particleQueues.get(ParticleRenderType.NO_RENDER);
        if (queue == null || queue.isEmpty()) {
            return;
        }
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        for (Particle particle : queue) {
            if (particle instanceof AreaParticle areaParticle) {
                areaParticle.tick();
                AreaParticle.render(areaParticle, cameraPos.x, cameraPos.y, cameraPos.z, matrix, partialTicks);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
            if (cameraEntity != null) {
                float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
                particleRenders(cameraEntity, event.getPoseStack(), partialTick);
            }
        }
    }
}
