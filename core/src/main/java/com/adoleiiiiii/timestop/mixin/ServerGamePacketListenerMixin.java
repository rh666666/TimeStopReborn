package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.common.TimeStopManager;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 时停且仅触发者可动时，拦截联机移动与输入包（不经过 {@link ServerPlayer#travel}）。
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerMixin {
    @Shadow
    public ServerPlayer player;

    @Inject(method = "handleMovePlayer", at = @At("HEAD"), cancellable = true)
    private void timestopreborn$blockNonOwnerMove(ServerboundMovePlayerPacket packet, CallbackInfo ci) {
        if (!TimeStopManager.canPlayerAct(this.player)) {
            ci.cancel();
        }
    }

    @Inject(method = "handlePlayerInput", at = @At("HEAD"), cancellable = true)
    private void timestopreborn$blockNonOwnerInput(ServerboundPlayerInputPacket packet, CallbackInfo ci) {
        if (!TimeStopManager.canPlayerAct(this.player)) {
            ci.cancel();
        }
    }
}
