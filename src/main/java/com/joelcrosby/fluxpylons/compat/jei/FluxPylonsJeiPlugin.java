package com.joelcrosby.fluxpylons.compat.jei;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.FluxPylonsItems;
import com.joelcrosby.fluxpylons.compat.jei.category.BoilerCategory;
import com.joelcrosby.fluxpylons.compat.jei.category.SmeltingCategory;
import com.joelcrosby.fluxpylons.compat.jei.category.WashingCategory;
import com.joelcrosby.fluxpylons.compat.jei.container.BoilerContainerHandler;
import com.joelcrosby.fluxpylons.compat.jei.container.SmelterContainerHandler;
import com.joelcrosby.fluxpylons.compat.jei.container.WasherContainerHandler;
import com.joelcrosby.fluxpylons.machine.BoilerGui;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;


import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JeiPlugin
public class FluxPylonsJeiPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "jei_plugin");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        var recipeRegistry = jeiRuntime.getRecipeManager();
        var recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        var hiddenRecipes = new ArrayList<RecipeHolder<CraftingRecipe>>();

        var itemNames = List.of(
            FluxPylonsItems.UPGRADE_EXTRACT.get(),
            FluxPylonsItems.UPGRADE_FLUID_EXTRACT.get(),
            FluxPylonsItems.UPGRADE_FILTER.get(),
            FluxPylonsItems.UPGRADE_FLUID_FILTER.get(),
            FluxPylonsItems.UPGRADE_TAG_FILTER.get(),
            FluxPylonsItems.UPGRADE_RETRIEVER.get(),
            FluxPylonsItems.UPGRADE_FLUID_RETRIEVER.get()
        );

        for (var item : itemNames) {
            var key = ResourceLocation.parse(item + "_clear_nbt");
            var holder = recipeManager.byKey(key);

            holder.ifPresent(h -> hiddenRecipes.add((RecipeHolder<CraftingRecipe>) h));
        }

        recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new SmeltingCategory(guiHelper));
        registration.addRecipeCategories(new WashingCategory(guiHelper));
        registration.addRecipeCategories(new BoilerCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SmeltingCategory.RECIPE_TYPE.get(), SmeltingCategory.getAllRecipes());
        registration.addRecipes(WashingCategory.RECIPE_TYPE.get(), WashingCategory.getAllRecipes());
        registration.addRecipes(BoilerCategory.RECIPE_TYPE.get(), BoilerCategory.getAllRecipes());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SmelterGui.class, new SmelterContainerHandler());
        registration.addGuiContainerHandler(WasherGui.class, new WasherContainerHandler());
        registration.addGuiContainerHandler(BoilerGui.class, new BoilerContainerHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.SMELTER.get()).copy(), SmeltingCategory.RECIPE_TYPE.get());
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.WASHER.get()).copy(), WashingCategory.RECIPE_TYPE.get());
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.BOILER.get()).copy(), BoilerCategory.RECIPE_TYPE.get());
    }


}
