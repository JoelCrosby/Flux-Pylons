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
import net.neoforged.neoforge.items.IItemHandler;

import java.util.HashMap;
import java.util.List;

import static com.joelcrosby.fluxpylons.recipe.common.RecipeCodecs.*;


public class SmelterRecipe extends BaseRecipe implements Recipe<RecipeInputContainer> {
    public static final RecipeType<SmelterRecipe> RECIPE_TYPE = FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING.get();

    public SmelterRecipe(List<Ingredient> ingredients, List<RecipeItemData> outputItems, long energy) {
        super(ingredients, null, outputItems, null, energy);
    }

    @Override
    public RecipeType<?> getType() {
        return RECIPE_TYPE;
    }

    protected static final HashMap<Integer, SmelterRecipe> recipeHashMap = new HashMap<>();

    public static final MapCodec<SmelterRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                    Ingredient.LIST_CODEC_NONEMPTY
                            .fieldOf("ingredients")
                            .forGetter(e -> e.ingredients),
                    RECIPE_ITEM_CODEC.listOf().fieldOf("result").forGetter(e -> e.outputItems),
                    Codec.LONG.fieldOf("energy").forGetter(e -> e.energy))
            .apply(builder, SmelterRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmelterRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), SmelterRecipe::getIngredients,
            RECIPE_ITEM_LIST_STREAM_CODEC, SmelterRecipe::getOutputItems,
            ByteBufCodecs.VAR_LONG, SmelterRecipe::getEnergy,
            SmelterRecipe::new
    );

    public static SmelterRecipe getRecipe(Level level, RecipeInputContainer input) {
        for (var recipe : level.getRecipeManager().getRecipes()) {
            if (recipe.value() instanceof SmelterRecipe smelterRecipe) {
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
    public RecipeSerializer<?> getSerializer() {
        return FluxPylonsRecipes.SMELTING.get();
    }

    public static class Serializer implements RecipeSerializer<SmelterRecipe> {
        @Override
        public MapCodec<SmelterRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, SmelterRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
