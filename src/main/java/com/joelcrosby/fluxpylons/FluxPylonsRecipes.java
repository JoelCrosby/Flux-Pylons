package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.recipe.ClearNbtRecipe;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import com.joelcrosby.fluxpylons.recipe.common.FluxPylonsRecipeType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class FluxPylonsRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FluxPylons.ID);

    public static final ResourceLocation RESOURCE_SMELTING = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "smelting");
    public static final ResourceLocation RESOURCE_WASHING = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "washing");
    public static final ResourceLocation RESOURCE_CLEAR_NBT = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "clear_nbt");


    public static final class FluxPylonsRecipeTypes {
        public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES_REGISTRY = DeferredRegister.create(Registries.RECIPE_TYPE, FluxPylons.ID);

        public static final Supplier<RecipeType<SmelterRecipe>> SMELTING = RECIPE_TYPES_REGISTRY.register("smelting",  () -> new FluxPylonsRecipeType<>(RESOURCE_SMELTING));
        public static final Supplier<RecipeType<WasherRecipe>> WASHING = RECIPE_TYPES_REGISTRY.register("washing",  () -> new FluxPylonsRecipeType<>(RESOURCE_WASHING));

        public static final Supplier<RecipeType<ClearNbtRecipe>> CLEAR_NBT = RECIPE_TYPES_REGISTRY.register("clear_nbt",  () -> new FluxPylonsRecipeType<>(RESOURCE_WASHING));
    }

    public static final Supplier<RecipeSerializer<?>> SMELTING = registerSerializer(RESOURCE_SMELTING, SmelterRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<?>> WASHING = registerSerializer(RESOURCE_WASHING, WasherRecipe.Serializer::new);

    public static final Supplier<RecipeSerializer<?>> CLEAR_NBT = registerSerializer(RESOURCE_CLEAR_NBT, ClearNbtRecipe.Serializer::new);

    private static Supplier<RecipeSerializer<?>> registerSerializer(ResourceLocation name, Supplier<RecipeSerializer<?>> serializer) {
        return RECIPE_SERIALIZERS.register(name.getPath(), serializer);
    }
}
