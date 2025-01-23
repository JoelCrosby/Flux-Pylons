package com.joelcrosby.fluxpylons.recipe;

import com.joelcrosby.fluxpylons.FluxPylonsRecipes;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import com.joelcrosby.fluxpylons.recipe.common.RecipeItemData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.List;

import static com.joelcrosby.fluxpylons.recipe.common.RecipeCodecs.*;


public class WasherRecipe extends BaseRecipe implements Recipe<RecipeInputContainer> {
    public static final RecipeType<WasherRecipe> RECIPE_TYPE = FluxPylonsRecipes.FluxPylonsRecipeTypes.WASHING.get();

    public WasherRecipe(List<SizedIngredient> ingredients, List<FluidIngredient> fluidIngredients, List<RecipeItemData> outputItems, long energy) {
        super(ingredients, fluidIngredients, outputItems, null, energy);
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    public static final MapCodec<WasherRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    SizedIngredient.FLAT_CODEC.listOf()
                            .fieldOf("ingredients")
                            .forGetter(e -> e.ingredients),
                    FluidIngredient.LIST_CODEC
                            .fieldOf("fluid_ingredients")
                            .forGetter(e -> e.fluidIngredients),
                    RECIPE_ITEM_CODEC.listOf().fieldOf("result").forGetter(e -> e.outputItems),
                    Codec.LONG.fieldOf("energy").forGetter(e -> e.energy))
            .apply(builder, WasherRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WasherRecipe> STREAM_CODEC = StreamCodec.composite(
            SizedIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), WasherRecipe::getItemIngredients,
            FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), WasherRecipe::getFluidIngredients,
            RECIPE_ITEM_LIST_STREAM_CODEC, WasherRecipe::getOutputItems,
            ByteBufCodecs.VAR_LONG, WasherRecipe::getEnergy,
            WasherRecipe::new
    );

    public static WasherRecipe getRecipe(Level level, RecipeInputContainer input) {
            for (var recipe : level.getRecipeManager().getRecipes()) {
                if (recipe.value() instanceof WasherRecipe washerRecipe) {
                    if (washerRecipe.matches(input, level)) {
                        var hash = input.hashCode();
                        recipeHashMap.put(hash, washerRecipe);
                    }
                }
            }

        var hash = input.hashCode();
        return recipeHashMap.get(hash);
    }



    @Override
    public RecipeSerializer<?> getSerializer() {
        return FluxPylonsRecipes.WASHING.get();
    }

    public static class Serializer implements RecipeSerializer<WasherRecipe> {
        @Override
        public MapCodec<WasherRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, WasherRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
