package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.data.InteractionSide;
import com.joelcrosby.fluxpylons.data.TagList;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.joelcrosby.fluxpylons.data.Codecs.*;

public class FluxPylonsDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE_DEFERRED_REGISTER
            = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FluxPylons.ID);

    private static final Supplier<DataComponentType<Boolean>> boolBuilder = () -> {
        DataComponentType.Builder<Boolean> stackBuilder = DataComponentType.builder();
        return stackBuilder.persistent(Codec.BOOL)
                .networkSynchronized(ByteBufCodecs.BOOL)
                .build();
    };

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_DENY_LIST = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("is-deny-list", boolBuilder);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> MATCH_NBT = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("match-nbt", boolBuilder);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<InteractionSide>> INTERACTION_SIDE = DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("interaction-side",
            () -> {
                DataComponentType.Builder<InteractionSide> stackBuilder = DataComponentType.builder();
                return stackBuilder.persistent(INTERACTION_SIDE_CODEC)
                        .networkSynchronized(INTERACTION_SIDE_STREAM_CODEC)
                        .build();
            }
    );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<TagList>> TAGS =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("tags",
                    () -> {
                        DataComponentType.Builder<TagList> stackBuilder = DataComponentType.builder();
                        return stackBuilder.persistent(TAG_LIST_CODEC)
                                .networkSynchronized(TAG_LIST_STREAM_CODEC)
                                .build();
                    }
            );

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ItemContainerContents>> INVENTORY =
            DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register("inventory",
                    () -> DataComponentType.<ItemContainerContents>builder()
                            .persistent(ItemContainerContents.CODEC)
                            .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                            .cacheEncoding()
                            .build()
            );
}

