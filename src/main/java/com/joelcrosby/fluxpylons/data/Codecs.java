package com.joelcrosby.fluxpylons.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.handler.codec.EncoderException;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Codecs {
    public static final StreamCodec<RegistryFriendlyByteBuf, List<String>> STRING_LIST_STREAM_CODEC = new StreamCodec<>() {

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull List<String> values) {

            int total = values.size();
            buf.writeVarInt(total);

            for (var value : values) {

                if (values.isEmpty())
                    throw new EncoderException("Empty String not allowed for chunk fluid!");
                ByteBufCodecs.STRING_UTF8.encode(buf, value);
            }
        }

        @Override
        public @NotNull List<String> decode(@NotNull RegistryFriendlyByteBuf buf) {

            int total = buf.readVarInt();
            List<String> stacks = new ArrayList<>();

            for (int i = 0; i < total; i++) {
                var value =  ByteBufCodecs.STRING_UTF8.decode(buf);
                stacks.add(value);
            }
            return stacks;
        }
    };

    public static final Codec<TagList> TAG_LIST_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Codec.STRING.listOf().fieldOf("tags").forGetter(TagList::tags)).apply(instance, TagList::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, TagList> TAG_LIST_STREAM_CODEC = StreamCodec.composite(
            STRING_LIST_STREAM_CODEC, TagList::tags,
            TagList::new
    );

    public static final Codec<InteractionSide> INTERACTION_SIDE_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(Direction.CODEC.fieldOf("direction").forGetter(InteractionSide::direction)).apply(instance, InteractionSide::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, InteractionSide> INTERACTION_SIDE_STREAM_CODEC = StreamCodec.composite(
            Direction.STREAM_CODEC, InteractionSide::direction,
            InteractionSide::new
    );
}
