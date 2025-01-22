package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public final class Events {

    @SubscribeEvent
    public static void onLevelTick(ServerTickEvent.Post e) {
        var levels = e.getServer().getAllLevels();

        for (var level : levels) {
            NetworkManager.get(level).getNetworks().forEach(n -> n.update(level));
            PylonNetworkManager.get(level).getNetworks().forEach(n -> n.update(level));
        }
    }
}
