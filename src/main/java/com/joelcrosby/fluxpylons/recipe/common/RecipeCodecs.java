package com.joelcrosby.fluxpylons.recipe.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeCodecs {

    public static final Codec<RecipeItemData> RECIPE_ITEM_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("item").forGetter(RecipeItemData::item),
            Codec.STRING.optionalFieldOf("tag").forGetter(RecipeItemData::tag),
            Codec.INT.optionalFieldOf("count", 1).forGetter(RecipeItemData::count),
            Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(RecipeItemData::chance)
    )
            .apply(instance, (item, tag, count, chance) -> new RecipeItemData(item, Optional.empty(), tag, count, chance)));

    public static final StreamCodec<RegistryFriendlyByteBuf, RecipeItemData> RECIPE_ITEM_STREAM_CODEC = new StreamCodec<>() {

        public void encode(RegistryFriendlyByteBuf buf, RecipeItemData ingredient) {
            buf.writeInt(ingredient.count());
            buf.writeFloat(ingredient.chance());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, ingredient.getItemStack());
        }

        public RecipeItemData decode(RegistryFriendlyByteBuf buf) {
            var count = buf.readInt();
            var chance = buf.readFloat();
            var itemstack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);

            itemstack.setCount(count);

            return new RecipeItemData(Optional.empty(), Optional.of(itemstack), Optional.empty(), count, chance);
        }
    };

    public static final StreamCodec<RegistryFriendlyByteBuf, List<RecipeItemData>> RECIPE_ITEM_LIST_STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(RegistryFriendlyByteBuf buf, List<RecipeItemData> items) {
            var total = items.size();
            buf.writeVarInt(total);

            for (var item : items) {
                RECIPE_ITEM_STREAM_CODEC.encode(buf, item);
            }
        }

        @Override
        public List<RecipeItemData> decode(RegistryFriendlyByteBuf buf) {
            var total = buf.readVarInt();
            var items = new ArrayList<RecipeItemData>();

            for (int i = 0; i < total; i++) {
                var item = RECIPE_ITEM_STREAM_CODEC.decode(buf);
                items.add(item);
            }

            return items;
        }
    };

    public static final Codec<RecipeFluidData> RECIPE_FLUID_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("fluid").forGetter(RecipeFluidData::fluid),
            Codec.STRING.optionalFieldOf("tag").forGetter(RecipeFluidData::tag),
            Codec.INT.optionalFieldOf("amount", 1).forGetter(RecipeFluidData::amount),
            Codec.FLOAT.optionalFieldOf("chance", 1.0f).forGetter(RecipeFluidData::chance)
    )
            .apply(instance, (fluid, tag, amount, chance) -> new RecipeFluidData(fluid, Optional.empty(), tag, amount, chance)));
}
