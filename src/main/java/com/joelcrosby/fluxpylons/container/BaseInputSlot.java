package com.joelcrosby.fluxpylons.container;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseInputSlot extends SlotItemHandler {
    public BaseInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public <C extends RecipeInput, T extends Recipe<C>> boolean checkRecipe(BaseRecipe recipe, ItemStack stack) {
        if (recipe == null) return false;

        for (var ingredient : recipe.ingredients) {
            for (var testStack : ingredient.getItems()) {
                if (stack.getItem() == testStack.getItem()) {
                    return true;
                }
            }
        }

        return false;
    }
}
