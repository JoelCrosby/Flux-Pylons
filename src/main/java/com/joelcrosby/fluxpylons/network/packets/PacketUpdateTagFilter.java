package com.joelcrosby.fluxpylons.network.packets;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

import static com.joelcrosby.fluxpylons.data.Codecs.STRING_LIST_STREAM_CODEC;

public record PacketUpdateTagFilter(boolean isDenyList, List<String> tags) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketUpdateTagFilter> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "update_tag_filter"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketUpdateTagFilter> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PacketUpdateTagFilter::isDenyList,
            STRING_LIST_STREAM_CODEC, PacketUpdateTagFilter::tags,
            PacketUpdateTagFilter::new);

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static class Handler {
        public static void handle(PacketUpdateTagFilter msg, IPayloadContext ctx) {
            ctx.enqueueWork(() -> {
                var player = ctx.player();

                var container = player.containerMenu;

                if (container instanceof TagFilterContainerMenu filterContainerMenu) {
                    var filterItem = filterContainerMenu.filterItem;
                    if (filterItem == null) return;
                    TagFilterItem.setIsDenyList(filterItem, msg.isDenyList);
                    TagFilterItem.setTags(filterItem, msg.tags);
                }
            });
        }
    }
}
