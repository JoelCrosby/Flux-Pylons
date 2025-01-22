package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
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

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.WASHING;

public class WashingCategory implements IRecipeCategory<RecipeHolder<WasherRecipe>> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable arrow;
    
    public static final Supplier<RecipeType<RecipeHolder<WasherRecipe>>> RECIPE_TYPE = RecipeType.createFromDeferredVanilla(WASHING);

    public WashingCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(WasherGui.TEXTURE, 40, 16, 132, 53).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.WASHER.get()));
        slotDrawable = guiHelper.getSlotDrawable();
        arrow = guiHelper.drawableBuilder(WasherGui.TEXTURE, 176, 0, 22, 15).buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<RecipeHolder<WasherRecipe>> getRecipeType(){
        return RECIPE_TYPE.get();
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("container." + FluxPylons.ID + "." + "washer");
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
    public void draw(RecipeHolder<WasherRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        arrow.draw(gui, 49, 18);
    }
    
    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, RecipeHolder<WasherRecipe> recipeHolder, IFocusGroup focusGroup) {
        var recipe = recipeHolder.value();

        var inputSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 26, 19);
        inputSlot.setSlotName(Component.translatable("terms.fluxpylons.input_slot").getString());
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, Arrays.stream(recipe.ingredients.getFirst().getItems()).toList());

        var fluidSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 2, 3);
        fluidSlot.setSlotName(Component.translatable("terms.fluxpylons.input_slot").getString());
        fluidSlot.addIngredients(NeoForgeTypes.FLUID_STACK, Arrays.stream(recipe.fluidIngredients.getFirst().getStacks()).toList());
        fluidSlot.setFluidRenderer(10_000, true, 16, 47);

        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var handler = recipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 87 + i * 18, 19);
            handler.setSlotName(Component.translatable("terms.fluxpylons.output_slot").getString());
            handler.addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.outputItems.get(i).getItemStack()));
        }
    }

    public static List<RecipeHolder<WasherRecipe>> getAllRecipes() {
        if (Minecraft.getInstance().level != null) {
            return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(WASHING.get());
        }

        return List.of();
    }
}
