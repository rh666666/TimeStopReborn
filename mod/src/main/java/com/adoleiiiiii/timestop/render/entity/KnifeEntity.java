package com.adoleiiiiii.timestop.render.entity;

import com.adoleiiiiii.timestop.common.EntityRegister;
import com.adoleiiiiii.timestop.common.ModSoundsRegister;
import com.adoleiiiiii.timestop.content.TimeStopDefaultContent;
import com.adoleiiiiii.timestop.common.TimeStopManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 飞刀投射物，时停期间由 TickRateManager 冻结 tick。
 */
public class KnifeEntity extends Arrow {
    public KnifeEntity(EntityType<? extends KnifeEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSoundsRegister.KNIFE_HIT.get();
    }

    public static KnifeEntity create(Level level, Player player) {
        KnifeEntity entity = new KnifeEntity(EntityRegister.FLYING_SWORD.get(), level);
        entity.setOwner(player);
        entity.setPos(player.position().add(0D, player.getEyeHeight(player.getPose()), 0D));
        return entity;
    }

    @Override
    public void tick() {
        if (TimeStopManager.isActive(level())) {
            return;
        }
        super.tick();
        if (!onGround() && !level().isClientSide()) {
            Vec3 pos = position();
            BlockHitResult hit = level().clip(new ClipContext(pos, pos.add(getDeltaMovement()),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
            if (hit.getType() == HitResult.Type.BLOCK) {
                BlockPos blockPos = hit.getBlockPos();
                Block block = level().getBlockState(blockPos).getBlock();
                if (block.equals(Blocks.COBWEB)) {
                    level().destroyBlock(blockPos, true);
                    setDeltaMovement(getDeltaMovement().scale(0.8D));
                }
                if (block.equals(Blocks.TRIPWIRE)) {
                    level().destroyBlock(blockPos, true);
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!TimeStopManager.isActive(level())) {
            if (result instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity living) {
                living.invulnerableTime = 0;
            }
            super.onHit(result);
        }
    }

    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(TimeStopDefaultContent.KNIFE.get());
    }
}
