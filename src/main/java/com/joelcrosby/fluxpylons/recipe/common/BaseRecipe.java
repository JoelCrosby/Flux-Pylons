package com.joelcrosby.fluxpylons.recipe.common;

import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseRecipe {
    public final long energy;
    public final NonNullList<Ingredient> ingredients;
    public final NonNullList<FluidIngredient> fluidIngredients;
    public final NonNullList<RecipeItemData> outputItems;
    public final NonNullList<RecipeFluidData> outputFluids;

    protected BaseRecipe(List<Ingredient> ingredients,
                         List<FluidIngredient> fluidIngredients,
                         List<RecipeItemData> outputItems,
                         List<RecipeFluidData> outputFluids,
                         long energy) {
        this.ingredients = ingredients == null ? NonNullList.create() : NonNullList.copyOf(ingredients);
        this.fluidIngredients = fluidIngredients == null ? NonNullList.create() : NonNullList.copyOf(fluidIngredients);
        this.outputItems = outputItems == null ? NonNullList.create() : NonNullList.copyOf(outputItems);
        this.outputFluids = outputFluids == null ? NonNullList.create() : NonNullList.copyOf(outputFluids);
        this.energy = energy;
    }

    protected static final HashMap<Integer, WasherRecipe> recipeHashMap = new HashMap<>();

    public long getEnergy() {
        return this.energy;
    }

    public List<RecipeItemData> getOutputItems() {
        return this.outputItems;
    }

    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return outputItems.stream().findFirst().orElseThrow().getItemStack();
    }

    public ItemStack assemble(RecipeInputContainer input, HolderLookup.Provider registries) {
        return outputItems.stream().findFirst().orElseThrow().getItemStack().copy();
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public List<ItemStack> getOutputItemStacks() {
        return this.outputItems.stream().map(RecipeItemData::getItemStack).collect(Collectors.toList());
    }

    public boolean matches(RecipeInputContainer input, Level worldIn) {
        if (input.isEmpty()) return false;

        var matchedItems = 0;
        var invSize = input.getContainerSize();

        for (int j = 0; j < invSize; j++) {
            var invItem = input.getItem(j);

            for (var inputItem : ingredients) {
                if (isValidItemStack(inputItem, invItem)) {
                    matchedItems++;
                }
            }
        }

        return ingredients.size() == matchedItems;
    }

    private boolean isValidItemStack(Ingredient ingredient, ItemStack toMatch) {
        var items = ingredient.getItems();

        for (var item : items) {
            var isSameItem = ItemStack.isSameItemSameComponents(item, toMatch);

            if (isSameItem && toMatch.getCount() >= item.getCount()) {
                return true;
            }
        }

        return false;
    }
}
