package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.pipe.PipeIoMode;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeContainerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketUpdatePipeManager(PipeIoMode ioMode) implements CustomPacketPayload {

    public static final Type<PacketUpdatePipeManager> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "update_pipe_manager"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdatePipeManager> STREAM_CODEC = StreamCodec.composite(
            PipeIoMode.STREAM_CODEC, PacketUpdatePipeManager::ioMode,
            PacketUpdatePipeManager::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(PacketUpdatePipeManager msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                var player = ctx.player();

                var container = player.containerMenu;

                if (container instanceof PipeUpgradeContainerMenu containerMenu) {
                    containerMenu.upgradeManager.setPipeIoMode(msg.ioMode);
                }
            });
        }
    }
}
