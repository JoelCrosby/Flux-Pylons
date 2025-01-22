package com.joelcrosby.fluxpylons.compat.jei;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlocks;
import com.joelcrosby.fluxpylons.compat.jei.category.SmeltingCategory;
import com.joelcrosby.fluxpylons.compat.jei.category.WashingCategory;
import com.joelcrosby.fluxpylons.compat.jei.container.SmelterContainerHandler;
import com.joelcrosby.fluxpylons.compat.jei.container.WasherContainerHandler;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;


import javax.annotation.Nonnull;

@JeiPlugin
public class FluxPylonsJeiPlugin implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "jei_plugin");
    }

//    @Override
//    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
//        var recipeRegistry = jeiRuntime.getRecipeManager();
//        var recipeManager = Minecraft.getInstance().level.getRecipeManager();
//        var hiddenRecipes = new ArrayList<CraftingRecipe>();
//
//        var itemNames = List.of(
//            FluxPylonsItems.UPGRADE_EXTRACT.get(),
//            FluxPylonsItems.UPGRADE_FLUID_EXTRACT.get(),
//            FluxPylonsItems.UPGRADE_FILTER.get(),
//            FluxPylonsItems.UPGRADE_FLUID_FILTER.get(),
//            FluxPylonsItems.UPGRADE_TAG_FILTER.get(),
//            FluxPylonsItems.UPGRADE_RETRIEVER.get(),
//            FluxPylonsItems.UPGRADE_FLUID_RETRIEVER.get()
//        );
//
//        for (var item : itemNames) {
//            var key = ResourceLocation.parse(item + "_clear_nbt");
//            var holder = recipeManager.byKey(key);
//            var recipe = holder.isPresent() ? holder.get(). : null;
//
//            manager.ifPresent((recipeHolder) -> hiddenRecipes.add((RecipeHolder<CraftingRecipe>) recipeHolder));
//        }
//
//        recipeRegistry.hideRecipes(RecipeTypes.CRAFTING, hiddenRecipes);
//    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new SmeltingCategory(guiHelper));
        registration.addRecipeCategories(new WashingCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SmeltingCategory.RECIPE_TYPE.get(), SmeltingCategory.getAllRecipes());
        registration.addRecipes(WashingCategory.RECIPE_TYPE.get(), WashingCategory.getAllRecipes());
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(SmelterGui.class, new SmelterContainerHandler());
        registration.addGuiContainerHandler(WasherGui.class, new WasherContainerHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.SMELTER.get()).copy(), SmeltingCategory.RECIPE_TYPE.get());
        registration.addRecipeCatalyst(new ItemStack(FluxPylonsBlocks.WASHER.get()).copy(), WashingCategory.RECIPE_TYPE.get());
    }


}
