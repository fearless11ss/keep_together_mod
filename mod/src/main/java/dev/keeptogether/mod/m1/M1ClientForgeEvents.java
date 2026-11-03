package dev.keeptogether.mod.m1;

import dev.keeptogether.mod.KeepTogetherMod;
import net.minecraft.client.server.IntegratedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
    modid = KeepTogetherMod.MOD_ID,
    bus = Mod.EventBusSubscriber.Bus.FORGE,
    value = Dist.CLIENT)
final class M1ClientForgeEvents {

    private M1ClientForgeEvents() {
    }

    @SubscribeEvent
    static void onServerStarted(ServerStartedEvent event) {
        if (event.getServer() instanceof IntegratedServer integratedServer) {
            M1TunnelController.openOwner(integratedServer);
        }
    }

    @SubscribeEvent
    static void onServerStopped(ServerStoppedEvent event) {
        M1TunnelController.closeOwner();
    }

    @SubscribeEvent
    static void onGameShuttingDown(GameShuttingDownEvent event) {
        M1TunnelController.closeAll();
    }
}
