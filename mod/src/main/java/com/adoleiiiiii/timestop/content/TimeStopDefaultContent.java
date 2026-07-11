package com.adoleiiiiii.timestop.content;

import com.adoleiiiiii.timestop.ClockItem;
import com.adoleiiiiii.timestop.KnifeItem;
import com.adoleiiiiii.timestop.TimeStopReborn;
import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.common.ModSoundsRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 默认模组内容注册：怀表、飞刀、创造页、实体与相关音效。
 */
public final class TimeStopDefaultContent {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TimeStopReborn.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TimeStopReborn.MODID);

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

    private TimeStopDefaultContent() {
    }

    /**
     * 向模组事件总线注册默认物品、实体与相关音效。
     *
     * @param modEventBus 模组事件总线
     */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        ModSoundsRegister.SOUNDS.register(modEventBus);
        EntityRegister.ENTITIES.register(modEventBus);
    }
}
