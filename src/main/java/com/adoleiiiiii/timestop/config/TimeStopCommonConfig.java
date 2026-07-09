package com.adoleiiiiii.timestop.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务端 common 配置：玩法规则与默认内容注册开关。
 */
public final class TimeStopCommonConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    static {
        BUILDER.push("gameplay");
    }

    public static final ModConfigSpec.BooleanValue ONLY_OWNER_CAN_MOVE = BUILDER
            .comment("When true, only the player who triggered time stop can move during time stop.")
            .translation("timestopreborn.configuration.gameplay.onlyOwnerCanMove")
            .define("onlyOwnerCanMove", false);

    static {
        BUILDER.pop();
        BUILDER.push("features");
    }

    public static final ModConfigSpec.BooleanValue REGISTER_DEFAULT_CONTENT = BUILDER
            .comment("When false, time clock, knife, creative tab, entities, sounds and nether star void craft are disabled.")
            .translation("timestopreborn.configuration.features.registerDefaultContent")
            .define("registerDefaultContent", true);

    static {
        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TimeStopCommonConfig() {
    }

    /** @return 时停期间是否仅触发者可移动 */
    public static boolean isOnlyOwnerCanMove() {
        return ONLY_OWNER_CAN_MOVE.get();
    }

    /** @return 配置是否注册默认模组内容与虚空转化 */
    public static boolean isRegisterDefaultContent() {
        return REGISTER_DEFAULT_CONTENT.get();
    }
}
