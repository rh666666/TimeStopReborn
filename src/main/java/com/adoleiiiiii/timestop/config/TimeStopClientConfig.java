package com.adoleiiiiii.timestop.config;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 客户端时停视觉配置：post 滤镜、启动动画开关与速度。
 */
public final class TimeStopClientConfig {
    public static final ResourceLocation DEFAULT_POST_EFFECT_ANIMATED =
            ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "shaders/post/the_world.json");
    public static final ResourceLocation DEFAULT_POST_EFFECT_STATIC =
            ResourceLocation.fromNamespaceAndPath(TimeStopReborn.MODID, "shaders/post/the_world_static.json");

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLE_POST_EFFECT = BUILDER
            .comment("Whether to apply a post-processing shader while time stop is active.")
            .translation("timestopreborn.configuration.visual.enablePostEffect")
            .define("visual.enablePostEffect", true);

    public static final ModConfigSpec.BooleanValue ENABLE_STARTUP_ANIMATION = BUILDER
            .comment(
                    "Whether to play the rewind startup animation (screen contraction / warp).",
                    "When false, the built-in default post effect uses a static grayscale filter."
            )
            .translation("timestopreborn.configuration.visual.enableStartupAnimation")
            .define("visual.enableStartupAnimation", true);

    public static final ModConfigSpec.DoubleValue STARTUP_ANIMATION_SPEED = BUILDER
            .comment("Per-frame increment of the rewind shader time uniform while time stop is active.")
            .translation("timestopreborn.configuration.visual.startupAnimationSpeed")
            .defineInRange("visual.startupAnimationSpeed", 0.01D, 0.001D, 0.05D);

    public static final ModConfigSpec.ConfigValue<String> POST_EFFECT = BUILDER
            .comment(
                    "ResourceLocation of the post chain JSON loaded during time stop.",
                    "Built-in presets (animation toggle applies to these two):",
                    "  timestopreborn:shaders/post/the_world.json (grayscale + startup animation)",
                    "  timestopreborn:shaders/post/the_world_static.json (grayscale only)",
                    "Other examples:",
                    "  minecraft:shaders/post/invert.json (vanilla invert)"
            )
            .translation("timestopreborn.configuration.visual.postEffect")
            .define("visual.postEffect", DEFAULT_POST_EFFECT_ANIMATED.toString());

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TimeStopClientConfig() {
    }

    /**
     * 解析配置中的 post effect 路径；内置预设随启动动画开关切换静/动版本。
     *
     * @return 可用于 {@link net.minecraft.client.renderer.GameRenderer#loadEffect} 的资源位置
     */
    public static ResourceLocation resolvePostEffect() {
        ResourceLocation parsed = parsePostEffect(POST_EFFECT.get());
        if (isBuiltInPostEffect(parsed)) {
            return isStartupAnimationEnabled() ? DEFAULT_POST_EFFECT_ANIMATED : DEFAULT_POST_EFFECT_STATIC;
        }
        return parsed;
    }

    private static ResourceLocation parsePostEffect(String raw) {
        ResourceLocation parsed = ResourceLocation.tryParse(raw);
        if (parsed == null) {
            TimeStopReborn.LOGGER.warn("Invalid visual.postEffect '{}', falling back to {}", raw, DEFAULT_POST_EFFECT_ANIMATED);
            return DEFAULT_POST_EFFECT_ANIMATED;
        }
        return parsed;
    }

    private static boolean isBuiltInPostEffect(ResourceLocation location) {
        return location.equals(DEFAULT_POST_EFFECT_ANIMATED) || location.equals(DEFAULT_POST_EFFECT_STATIC);
    }

    /** @return 配置是否启用时停 post 滤镜 */
    public static boolean isPostEffectEnabled() {
        return ENABLE_POST_EFFECT.get();
    }

    /** @return 是否播放 rewind 启动动画并驱动 time uniform */
    public static boolean isStartupAnimationEnabled() {
        return ENABLE_STARTUP_ANIMATION.get();
    }

    /** @return 启动动画 time uniform 每帧增量 */
    public static float getStartupAnimationSpeed() {
        return STARTUP_ANIMATION_SPEED.get().floatValue();
    }
}
