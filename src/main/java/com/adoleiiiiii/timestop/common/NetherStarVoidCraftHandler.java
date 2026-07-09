package com.adoleiiiiii.timestop.common;

import com.adoleiiiiii.timestop.TimeStopReborn;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 下界之星落入虚空时转化为时间怀表。
 */
public final class NetherStarVoidCraftHandler {
    private static int lastServerTick = -1;
    private static final Set<UUID> clockGrantedPlayerTicks = new HashSet<>();

    private NetherStarVoidCraftHandler() {
    }

    /** 处理下界之星落入虚空；非下界之星仍按原版销毁。 */
    public static void onVoidEntry(ServerLevel level, ItemEntity itemEntity) {
        ItemStack stack = itemEntity.getItem();
        if (stack.isEmpty() || !stack.is(Items.NETHER_STAR)) {
            itemEntity.discard();
            return;
        }
        craftTimeClock(level, itemEntity, stack);
    }

    /** 消耗至多一颗下界之星换得怀表；同 tick 内同一玩家仅转化一次，其余下界之星一次性返还。 */
    private static void craftTimeClock(ServerLevel level, ItemEntity itemEntity, ItemStack stack) {
        int serverTick = level.getServer().getTickCount();
        if (serverTick != lastServerTick) {
            clockGrantedPlayerTicks.clear();
            lastServerTick = serverTick;
        }

        int totalStars = stack.getCount();
        itemEntity.discard();

        Player recipient = resolveRecipient(itemEntity);
        UUID playerId = recipient != null ? recipient.getUUID() : null;
        boolean grantClock = playerId == null || !clockGrantedPlayerTicks.contains(playerId);
        if (grantClock && playerId != null) {
            clockGrantedPlayerTicks.add(playerId);
        }

        int starsToReturn = grantClock ? totalStars - 1 : totalStars;

        double x = itemEntity.getX();
        double y = level.getMinBuildHeight();
        double z = itemEntity.getZ();
        if (recipient != null) {
            if (grantClock) {
                giveOrDrop(recipient, new ItemStack(TimeStopReborn.CLOCK.get()));
                recipient.sendSystemMessage(Component.translatable("message.timestopreborn.void_craft.time_clock"));
            }
            if (starsToReturn > 0) {
                giveOrDrop(recipient, new ItemStack(Items.NETHER_STAR, starsToReturn));
            }
        } else {
            if (grantClock) {
                spawnItem(level, x, y, z, new ItemStack(TimeStopReborn.CLOCK.get()));
            }
            if (starsToReturn > 0) {
                spawnItem(level, x, y, z, new ItemStack(Items.NETHER_STAR, starsToReturn));
            }
        }

        level.playSound(
                null,
                x,
                itemEntity.getY(),
                z,
                SoundEvents.END_PORTAL_SPAWN,
                SoundSource.PLAYERS,
                0.8F,
                1.2F
        );
        level.sendParticles(
                ParticleTypes.REVERSE_PORTAL,
                x,
                itemEntity.getY(),
                z,
                32,
                0.2D,
                0.2D,
                0.2D,
                0.05D
        );
    }

    private static void giveOrDrop(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }

    private static void spawnItem(ServerLevel level, double x, double y, double z, ItemStack stack) {
        ItemEntity drop = new ItemEntity(level, x, y, z, stack);
        drop.setDefaultPickUpDelay();
        level.addFreshEntity(drop);
    }

    @Nullable
    private static Player resolveRecipient(ItemEntity itemEntity) {
        Entity owner = itemEntity.getOwner();
        if (owner instanceof Player player) {
            return player;
        }
        if (itemEntity.getTarget() != null) {
            Entity target = ((ServerLevel) itemEntity.level()).getEntity(itemEntity.getTarget());
            if (target instanceof Player player) {
                return player;
            }
        }
        return null;
    }
}
