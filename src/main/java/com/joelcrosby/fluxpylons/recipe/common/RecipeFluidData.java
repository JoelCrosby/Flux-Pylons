package com.joelcrosby.fluxpylons.recipe.common;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public record RecipeFluidData(Optional<String> fluid, Optional<FluidStack> fluidStack, Optional<String> tag, int amount, float chance) {
    public FluidStack getFluidStack() {
        if (fluidStack.isPresent()) {
            return fluidStack.get();
        }

        if (this.tag.isPresent()) {
            var set = getItemsFromTag(this.tag.get(), this.amount);

            if (set.isPresent()) {
                var fromTag = set.get().stream().findFirst().orElseThrow().value();
                return new FluidStack(fromTag, this.amount);
            }
        }

        if (this.fluid.isPresent()) {
            var res = ResourceLocation.bySeparator(this.fluid.get(), ':');
            var exists = BuiltInRegistries.FLUID.containsKey(res);

            if (!exists) {
                throw new IllegalStateException("Invalid recipe ingredient object: " + this.fluid + " | " + this.tag + " does not exist!");
            }

            var single = BuiltInRegistries.FLUID.get(res);

            return new FluidStack(single, this.amount);
        }

        throw new IllegalStateException("Invalid recipe ingredient object: " + this.fluid + " | " + this.tag + " does not exist!");
    }

    private static Optional<HolderSet.Named<Fluid>> getItemsFromTag(String tag, int count) {
        var res = ResourceLocation.bySeparator(tag, ':');
        var tagKey = TagKey.create(Registries.FLUID, res);
        var holderSet = BuiltInRegistries.FLUID.getOrCreateTag(tagKey);
        var set = new AtomicReference<>(new ArrayList<>());

        holderSet.stream().forEach(holder -> set.get().add(new FluidStack(holder.value(), count)));

        return Optional.of(holderSet);
    }
}