package com.joelcrosby.fluxpylons.compat.jei.container;

import com.google.common.collect.Lists;
import com.joelcrosby.fluxpylons.compat.jei.category.WashingCategory;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WasherContainerHandler implements IGuiContainerHandler<WasherGui> {
    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull WasherGui containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public @NotNull Rect2i getArea() {
                return new Rect2i(90, 35, 22, 15);
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip) {
                tooltip.add(Component.literal("Show Recipes"));
                tooltip.addAll(containerScreen.getTooltips());
            }

            @Override
            public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
                recipesGui.showTypes(List.of(WashingCategory.RECIPE_TYPE.get()));
            }
        });

        return areas;
    }
}
