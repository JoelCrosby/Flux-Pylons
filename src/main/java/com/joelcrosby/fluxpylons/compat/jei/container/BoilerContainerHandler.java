package com.joelcrosby.fluxpylons.compat.jei.container;

import com.joelcrosby.fluxpylons.compat.jei.category.BoilerCategory;
import com.joelcrosby.fluxpylons.machine.BoilerGui;
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

public class BoilerContainerHandler implements IGuiContainerHandler<BoilerGui> {
    @Override
    public @NotNull Collection<IGuiClickableArea> getGuiClickableAreas(@NotNull BoilerGui containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public @NotNull Rect2i getArea() {
                return new Rect2i(80, 23, 15, 16);
            }

            @Override
            public void getTooltip(@NotNull ITooltipBuilder tooltip) {
                tooltip.add(Component.literal("Show Fuel Items"));
                tooltip.addAll(containerScreen.getTooltips());
            }

            @Override
            public void onClick(@NotNull IFocusFactory focusFactory, @NotNull IRecipesGui recipesGui) {
                recipesGui.showTypes(List.of(BoilerCategory.RECIPE_TYPE.get()));
            }
        });

        return areas;
    }
}
