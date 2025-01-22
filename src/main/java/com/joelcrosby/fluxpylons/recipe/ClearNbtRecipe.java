package com.joelcrosby.fluxpylons.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;

public class ClearNbtRecipe extends ShapelessRecipe {
    public ClearNbtRecipe(String group, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, CraftingBookCategory.MISC, result, ingredients);
    }
}
