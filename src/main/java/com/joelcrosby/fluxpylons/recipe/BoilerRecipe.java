package com.joelcrosby.fluxpylons.recipe;

import com.joelcrosby.fluxpylons.FluxPylonsRecipes;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;


public class BoilerRecipe implements Recipe<RecipeInputContainer> {
    public static final RecipeType<BoilerRecipe> RECIPE_TYPE = FluxPylonsRecipes.FluxPylonsRecipeTypes.BOILER.get();

    public Ingredient fuel;
    public int temperature;
    public int ticks;

    public BoilerRecipe(Ingredient fuel, int temperature, int ticks) {
        this.fuel = fuel;
        this.temperature = temperature;
        this.ticks = ticks;
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    protected static final HashMap<Integer, BoilerRecipe> recipeHashMap = new HashMap<>();

    public static final MapCodec<BoilerRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Ingredient.CODEC.fieldOf("fuel").forGetter(e -> e.fuel),
                    Codec.INT.fieldOf("temperature").forGetter(e -> e.temperature),
                    Codec.INT.fieldOf("ticks").forGetter(e -> e.ticks))
            .apply(builder, BoilerRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BoilerRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, BoilerRecipe::getItem,
            ByteBufCodecs.VAR_INT, BoilerRecipe::getTemperature,
            ByteBufCodecs.VAR_INT, BoilerRecipe::getTicks,
            BoilerRecipe::new
    );

    public static BoilerRecipe getRecipe(Level level, RecipeInputContainer input) {
        for (var recipe : level.getRecipeManager().getRecipes()) {
            if (recipe.value() instanceof BoilerRecipe smelterRecipe) {
                if (smelterRecipe.matches(input, level)) {
                    var hash = input.hashCode();
                    recipeHashMap.put(hash, smelterRecipe);
                }
            }
        }

        var hash = input.hashCode();
        return recipeHashMap.get(hash);
    }

    @Override
    public boolean matches(RecipeInputContainer input, Level level) {
        if (input.isEmpty()) return false;

        var matchedItems = 0;
        var invSize = input.getContainerSize();
        var itemstack = Arrays.stream(fuel.getItems()).findFirst().orElse(null);

        if (itemstack == null) return false;

        for (int j = 0; j < invSize; j++) {
            var invItem = input.getItem(j);

            if (ItemStack.isSameItem(itemstack, invItem)) {
                matchedItems++;
            }
        }

        return matchedItems > 0;
    }

    @Override
    @Nullable
    public ItemStack assemble(RecipeInputContainer recipeInputContainer, HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }

    @Override
    @Nullable
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FluxPylonsRecipes.BOILER.get();
    }

    public Ingredient getItem() {
        return fuel;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getTicks() {
        return ticks;
    }

    public static class Serializer implements RecipeSerializer<BoilerRecipe> {
        @Override
        public MapCodec<BoilerRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BoilerRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
