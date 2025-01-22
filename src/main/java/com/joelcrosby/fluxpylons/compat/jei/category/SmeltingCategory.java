package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING;

public class SmeltingCategory implements IRecipeCategory<RecipeHolder<SmelterRecipe>> {
    private final IDrawable background;
    private final IDrawable icon;
    
    private final IDrawable slotDrawable;
    
    private final IDrawable arrow;
    
    public static final Supplier<RecipeType<RecipeHolder<SmelterRecipe>>> RECIPE_TYPE = RecipeType.createFromDeferredVanilla(SMELTING);

    public SmeltingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 27, 22, 144, 40).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.SMELTER.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(SmelterGui.TEXTURE, 176, 0, 22, 15).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<RecipeHolder<SmelterRecipe>> getRecipeType(){
        return RECIPE_TYPE.get();
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("container." + FluxPylons.ID + "." + "smelter");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<SmelterRecipe> recipeHolder, IFocusGroup focuses) {
        var recipe = recipeHolder.value();

        for (var i = 0; i < recipe.ingredients.size(); i++) {
            var handler = builder.addSlot(RecipeIngredientRole.INPUT, 3 + i * 18, 3);
            handler.setSlotName(Component.translatable("terms.fluxpylons.input_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(recipe.ingredients.get(i).getItems()).toList());
        }

        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var handler = builder.addSlot(RecipeIngredientRole.OUTPUT, 101 + i * 18, 13);
            handler.setSlotName(Component.translatable("terms.fluxpylons.output_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, recipe.getOutputItemStacks());
        }
    }

    @Override
    public void draw(RecipeHolder<SmelterRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        arrow.draw(gui, 62, 12);
    }
    
    public static List<RecipeHolder<SmelterRecipe>> getAllRecipes() {
        if (Minecraft.getInstance().level != null) {
            return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(SMELTING.get());
        }

        return List.of();
    }
}
