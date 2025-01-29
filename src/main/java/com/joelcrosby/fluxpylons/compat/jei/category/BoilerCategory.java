package com.joelcrosby.fluxpylons.compat.jei.category;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.machine.BoilerGui;
import com.joelcrosby.fluxpylons.recipe.BoilerRecipe;
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

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.BOILER;

public class BoilerCategory implements IRecipeCategory<RecipeHolder<BoilerRecipe>> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;
    
public static final Supplier<RecipeType<RecipeHolder<BoilerRecipe>>> RECIPE_TYPE = RecipeType.createFromDeferredVanilla(BOILER);

    public BoilerCategory(IGuiHelper guiHelper) {
        background = guiHelper.drawableBuilder(BoilerGui.TEXTURE, 41, 21, 94, 49).build();
        icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(FluxPylonsBlocks.BOILER.get()));
        arrow = guiHelper.drawableBuilder(BoilerGui.TEXTURE, 176, 0, 14, 14).buildAnimated(400, IDrawableAnimated.StartDirection.TOP, true);
    }

    @Override
    public RecipeType<RecipeHolder<BoilerRecipe>> getRecipeType() {
        return RECIPE_TYPE.get();
    }
    
    @Override
    public Component getTitle() {
        return Component.translatable("jei." + FluxPylons.ID + "." + "boiler_fuel");
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
    public void draw(RecipeHolder<BoilerRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics gui, double mouseX, double mouseY) {
        arrow.draw(gui, 39, 3);

        var recipe = recipeHolder.value();

        if (Utility.isHovering(3, 43, 90, 4, mouseX, mouseY)) {
            drawToolTips(recipe, gui, mouseX, mouseY);
        }

        drawTemperatureBar(gui, recipe);
    }

    private void drawTemperatureBar(GuiGraphics gui, BoilerRecipe recipe) {
        var temperature = recipe.getTemperature();

        var xOffset = 0;
        var yOffset = 166;

        var x = 3;
        var y = 44;

        var uWidth = getTemperatureBar(88, temperature);
        var uHeight = 4;

        gui.blit(BoilerGui.TEXTURE, x, y, xOffset, yOffset, uWidth, uHeight);
    }

    public int getTemperatureBar(int widthPx, int temperature) {
        var max = 1500;
        return (((temperature * 100 / max * 100) / 100) * widthPx) / 100;
    }

    private void drawToolTips(BoilerRecipe recipe, GuiGraphics gui, double mouseX, double mouseY)
    {
        var temperature = recipe.getTemperature();
        var formatter = new DecimalFormat("#,###");
        var stringTemperature = formatter.format(temperature);

        List<Component> components = List.of(
                Component.translatable("info." + FluxPylons.ID + ".max_temperature").append(": "),
                Component.literal(stringTemperature).append(" ").append(Component.translatable("info." + FluxPylons.ID + ".celsius"))
        );

        var minecraft = Minecraft.getInstance();
        gui.renderTooltip(minecraft.font, components, Optional.empty(), (int) mouseX, (int) mouseY);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder recipeLayout, RecipeHolder<BoilerRecipe> recipeHolder, IFocusGroup focusGroup) {
        var recipe = recipeHolder.value();

        var inputSlot = recipeLayout.addSlot(RecipeIngredientRole.INPUT, 21, 20);
        inputSlot.setSlotName(Component.translatable("terms.fluxpylons.input_slot").getString());
        inputSlot.addIngredients(VanillaTypes.ITEM_STACK, List.of(recipe.fuel.getItems()));
    }

    public static List<RecipeHolder<BoilerRecipe>> getAllRecipes() {
        if (Minecraft.getInstance().level != null) {
            return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(BOILER.get());
        }

        return List.of();
    }
}
