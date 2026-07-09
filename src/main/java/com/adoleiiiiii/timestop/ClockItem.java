package com.adoleiiiiii.timestop;

import com.adoleiiiiii.timestop.api.TimeStopAPI;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 怀表物品，右键在服务端切换时停并同步客户端视觉。
 */
public class ClockItem extends Item {
    public ClockItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            if (player.getCooldowns().isOnCooldown(this)) {
                return InteractionResultHolder.fail(stack);
            }
            TimeStopAPI.toggle(player.getServer(), serverPlayer);
            player.getCooldowns().addCooldown(this, 30);
        }
        return InteractionResultHolder.success(stack);
    }
}
