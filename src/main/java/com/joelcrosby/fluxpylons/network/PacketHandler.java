package com.joelcrosby.fluxpylons.network;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.network.packets.*;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public class PacketHandler {

    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(FluxPylons.ID);

        registrar.playToServer(PacketGhostSlot.TYPE, PacketGhostSlot.STREAM_CODEC, PacketGhostSlot.Handler::handle);
        registrar.playToServer(PacketOpenScreen.TYPE, PacketOpenScreen.STREAM_CODEC, PacketOpenScreen.Handler::handle);
        registrar.playToServer(PacketUpdateFilter.TYPE, PacketUpdateFilter.STREAM_CODEC, PacketUpdateFilter.Handler::handle);
        registrar.playToServer(PacketUpdateTagFilter.TYPE, PacketUpdateTagFilter.STREAM_CODEC, PacketUpdateTagFilter.Handler::handle);
        registrar.playToServer(PacketUpdatePipeManager.TYPE, PacketUpdatePipeManager.STREAM_CODEC, PacketUpdatePipeManager.Handler::handle);
    }
}
