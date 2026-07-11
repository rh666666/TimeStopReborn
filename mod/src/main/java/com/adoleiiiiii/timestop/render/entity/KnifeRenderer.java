package com.adoleiiiiii.timestop.render.entity;

import com.adoleiiiiii.timestop.TimeStopReborn;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;

/**
 * 飞刀实体渲染器。
 */
public class KnifeRenderer extends EntityRenderer<KnifeEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "textures/entity/knife.png");

    public KnifeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(KnifeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(KnifeEntity entity, float yRotation, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1F, 1.4F, 1.4F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));
        float tex1x1 = 0.0F;
        float tex1x2 = 0.5F;
        float tex1x2h = 0.125F;
        float tex1y1 = 0.0F;
        float tex1y2 = 0.15625F;
        float tex2x1 = 0.0F;
        float tex2x2 = 0.15625F;
        float tex2y1 = 0.15625F;
        float tex2y2 = 0.3125F;
        float scale = 0.0375F;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-4.0D, 0.0D, 0.0D);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        PoseStack.Pose entry = poseStack.last();
        Matrix4f pose = entry.pose();
        vertex(pose, consumer, -4, -2, -2, tex2x1, tex2y1, -1, 0, 0, packedLight);
        vertex(pose, consumer, -4, -2, 2, tex2x2, tex2y1, -1, 0, 0, packedLight);
        vertex(pose, consumer, -4, 2, 2, tex2x2, tex2y2, -1, 0, 0, packedLight);
        vertex(pose, consumer, -4, 2, -2, tex2x1, tex2y2, -1, 0, 0, packedLight);
        vertex(pose, consumer, -4, 2, -2, tex2x1, tex2y1, 1, 0, 0, packedLight);
        vertex(pose, consumer, -4, 2, 2, tex2x2, tex2y1, 1, 0, 0, packedLight);
        vertex(pose, consumer, -4, -2, 2, tex2x2, tex2y2, 1, 0, 0, packedLight);
        vertex(pose, consumer, -4, -2, -2, tex2x1, tex2y2, 1, 0, 0, packedLight);
        for (int j = 0; j < 4; ++j) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            vertex(pose, consumer, -8, -2, 0, tex1x1, tex1y1, 0, 1, 0, packedLight);
            vertex(pose, consumer, j % 2 == 1 ? 8 : -4, -2, 0, j % 2 == 1 ? tex1x2 : tex1x2h, tex1y1, 0, 1, 0, packedLight);
            vertex(pose, consumer, j % 2 == 1 ? 8 : -4, 2, 0, j % 2 == 1 ? tex1x2 : tex1x2h, tex1y2, 0, 1, 0, packedLight);
            vertex(pose, consumer, -8, 2, 0, tex1x1, tex1y2, 0, 1, 0, packedLight);
        }
        poseStack.popPose();
    }

    private static void vertex(Matrix4f pose, VertexConsumer consumer,
                               int x, int y, int z, float u, float v,
                               int nx, int ny, int nz, int light) {
        consumer.addVertex(pose, x, y, z)
                .setColor(255, 255, 255, 255)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal((float) nx, (float) ny, (float) nz);
    }
}
