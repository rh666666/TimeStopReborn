package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopFeatureGate;
import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.common.SoundsRegister;
import com.adoleiiiiii.timestop.config.TimeStopClientConfig;
import com.adoleiiiiii.timestop.config.TimeStopCommonConfig;
import com.adoleiiiiii.timestop.network.TimeStopPackets;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

/**
 * 模组主入口，负责注册物品、实体、音效与创造模式页。
 */
@Mod(TimeStopReborn.MODID)
public class TimeStopReborn {
    public static final String MODID = "timestopreborn";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredItem<ClockItem> CLOCK = ITEMS.registerItem("time_clock", ClockItem::new,
            new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant().stacksTo(1));

    public static final DeferredItem<KnifeItem> KNIFE = ITEMS.registerItem("knife", KnifeItem::new,
            new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant().stacksTo(16)
                    .attributes(KnifeItem.createAttributes()));

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_TAB = CREATIVE_MODE_TABS.register("mod_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> CLOCK.get().getDefaultInstance())
                    .title(Component.translatable("itemGroup.timestopreborn").withStyle(ChatFormatting.YELLOW))
                    .displayItems((parameters, output) -> {
                        output.accept(CLOCK.get());
                        output.accept(KNIFE.get());
                    }).build());

    public TimeStopReborn(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, TimeStopClientConfig.SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, TimeStopCommonConfig.SPEC);
        modEventBus.addListener(TimeStopReborn::onConfigLoad);
        modEventBus.addListener(TimeStopReborn::onConfigReload);
        modEventBus.addListener(TimeStopPackets::register);
        modEventBus.addListener(TimeStopRebornClient::onConfigReload);
        registerContent(modEventBus);
    }

    private static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() != TimeStopCommonConfig.SPEC) {
            return;
        }
        TimeStopFeatureGate.INSTANCE.applyConfig(TimeStopCommonConfig.isRegisterDefaultContent());
    }

    private static void registerContent(IEventBus modEventBus) {
        if (!TimeStopFeatureGate.registerDefaultContent()) {
            return;
        }
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        SoundsRegister.SOUNDS.register(modEventBus);
        EntityRegister.ENTITIES.register(modEventBus);
    }

    private static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == TimeStopCommonConfig.SPEC) {
            TimeStopFeatureGate.INSTANCE.applyConfig(TimeStopCommonConfig.isRegisterDefaultContent());
        }
    }
}
