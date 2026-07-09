package com.adoleiiiiii.timestop.api;

/**
 * 控制默认内容（物品、实体、音效与下界之星虚空转化）是否启用；API 与 common 配置取 AND。
 */
public final class TimeStopFeatureGate {
    public static final TimeStopFeatureGate INSTANCE = new TimeStopFeatureGate();

    private boolean apiRegisterDefaultContent = true;
    private boolean configRegisterDefaultContent = true;

    private TimeStopFeatureGate() {
    }

    /** 关闭默认物品、创造页、实体、音效与下界之星虚空转化。 */
    public void disableDefaultContent() {
        apiRegisterDefaultContent = false;
    }

    /** 开启默认内容（API 侧）。 */
    public void enableDefaultContent() {
        apiRegisterDefaultContent = true;
    }

    /**
     * 由 common 配置加载时刷新 config 侧开关；不影响 API 覆盖。
     *
     * @param registerDefaultContent 配置是否注册默认内容与虚空转化
     */
    public void applyConfig(boolean registerDefaultContent) {
        configRegisterDefaultContent = registerDefaultContent;
    }

    /** @return 是否注册怀表、飞刀等默认内容并启用虚空转化 */
    public boolean isRegisterDefaultContent() {
        return apiRegisterDefaultContent && configRegisterDefaultContent;
    }

    /** @return 是否注册怀表、飞刀等默认内容并启用虚空转化 */
    public static boolean registerDefaultContent() {
        return INSTANCE.isRegisterDefaultContent();
    }
}
