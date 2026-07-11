package com.adoleiiiiii.timestop.mixin;

import com.adoleiiiiii.timestop.Time;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 时停期间主机客户端 pause 为 true 时，集成服会仅执行 {@code tickPaused} 而不 tick 网络，
 * 导致 LAN 客户端在主机退出时收不到断线包。开放 LAN 且时停激活时仍按未暂停处理集成服 tick。
 */
@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    /**
     * 开放 LAN 且客户端时停激活时，不把集成服视为暂停，保证 {@code connection.tick()} 持续运行。
     *
     * @param instance 主机客户端
     * @return 是否应使集成服进入仅统计在线时长的暂停分支
     */
    @Redirect(method = "tickServer", require = 1, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;isPaused()Z"))
    private boolean timestopreborn$keepLanNetworkingDuringTimeStop(Minecraft instance) {
        IntegratedServer server = (IntegratedServer) (Object) this;
        if (Time.isClientActive() && server.isPublished()) {
            return false;
        }
        return instance.isPaused();
    }
}
