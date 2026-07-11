package com.adoleiiiiii.timestop.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 服务端 common 配置：玩法规则。
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
    }

    public static final ModConfigSpec SPEC = BUILDER.build();

    private TimeStopCommonConfig() {
    }

    /** @return 时停期间是否仅触发者可移动 */
    public static boolean isOnlyOwnerCanMove() {
        return ONLY_OWNER_CAN_MOVE.get();
    }
}
