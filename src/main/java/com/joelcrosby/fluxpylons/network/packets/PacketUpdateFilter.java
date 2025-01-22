package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketUpdateFilter(boolean isDenyList, boolean matchNbt, Direction interactionSide) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketUpdateFilter> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "update_filter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateFilter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PacketUpdateFilter::isDenyList,
            ByteBufCodecs.BOOL, PacketUpdateFilter::matchNbt,
            Direction.STREAM_CODEC, PacketUpdateFilter::interactionSide,
            PacketUpdateFilter::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public static class Handler {
        public static void handle(final PacketUpdateFilter msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                var player = ctx.player();

                var container = player.containerMenu;

                if (container instanceof BaseFilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    BaseFilterItem.setIsDenyList(filterItem, msg.isDenyList);
                    BaseFilterItem.setMatchNbt(filterItem, msg.matchNbt);
                    BaseFilterItem.setInteractionSide(filterItem, msg.interactionSide);
                }
            });
        }
    }
}
