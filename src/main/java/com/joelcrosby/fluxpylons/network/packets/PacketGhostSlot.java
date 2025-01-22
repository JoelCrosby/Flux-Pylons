package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FilterSlotHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PacketGhostSlot(int slotNumber, ItemStack stack, int count) implements CustomPacketPayload {

    public static final Type<PacketGhostSlot> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "ghost_slot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGhostSlot> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketGhostSlot::slotNumber,
            ItemStack.OPTIONAL_STREAM_CODEC, PacketGhostSlot::stack,
            ByteBufCodecs.INT, PacketGhostSlot::count,
            PacketGhostSlot::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(PacketGhostSlot msg, final IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                var sender = ctx.player();

                var container = sender.containerMenu;

                var slot = container.slots.get(msg.slotNumber);
                var stack = msg.stack;

                stack.setCount(msg.count);

                if (slot instanceof FilterSlotHandler) {
                    slot.set(stack);
                }
            });
        }
    }
}
