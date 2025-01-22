package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record PacketOpenScreen(int slotNumber) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketOpenScreen> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "open_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketOpenScreen> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketOpenScreen::slotNumber,
            PacketOpenScreen::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(PacketOpenScreen msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                var sender = ctx.player();

                var container = sender.containerMenu;

                var slot = container.slots.get(msg.slotNumber);
                var stack = slot.getItem();
                var stackItem = stack.getItem();

                if (stackItem instanceof BaseFilterItem filterItem) {
                    filterItem.openGui(sender, stack);
                }
            });
        }
    }
}
