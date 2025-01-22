package com.joelcrosby.fluxpylons.recipe.common;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record RecipeItemData(Optional<String> item, Optional<ItemStack> itemStack, Optional<String> tag, int count, float chance) {
    public ItemStack getItemStack() {
        if (itemStack.isPresent()) {
            return itemStack.get();
        }

        if (this.tag.isPresent()) {
            var set = getItemsFromTag(this.tag.get(), this.count);

            if (set.isPresent()) {
                var itemFromTag = set.get().stream().findFirst().orElseThrow().value();
                return new ItemStack(itemFromTag, this.count);
            }
        }

        if (this.item.isPresent()) {
            var res = ResourceLocation.bySeparator(this.item.get(), ':');
            var exists = BuiltInRegistries.ITEM.containsKey(res);

            if (!exists) {
                throw new IllegalStateException("Invalid recipe ingredient object: " + this.item + " | " + this.tag + " does not exist!");
            }

            var single = BuiltInRegistries.ITEM.get(res);

            return new ItemStack(single, this.count);
        }

        throw new IllegalStateException("Invalid recipe ingredient object: " + this.item + " | " + this.tag + " does not exist!");
    }

    public Pair<ItemStack, Float> getItemStackWithChance() {
        return new Pair<>(getItemStack(), chance);
    }

    private static Optional<HolderSet.Named<Item>> getItemsFromTag(String tag, int count) {
        var res = ResourceLocation.bySeparator(tag, ':');
        var tagKey = TagKey.create(Registries.ITEM, res);
        var holderSet = BuiltInRegistries.ITEM.getOrCreateTag(tagKey);
        var set = new AtomicReference<>(new ArrayList<>());

        holderSet.stream().forEach(holder -> set.get().add(new ItemStack(holder.value(), count)));

        return Optional.of(holderSet);
    }
}