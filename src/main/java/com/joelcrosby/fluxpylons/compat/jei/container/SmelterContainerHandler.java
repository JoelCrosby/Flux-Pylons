package com.joelcrosby.fluxpylons.compat.jei.container;

import com.google.common.collect.Lists;
import com.joelcrosby.fluxpylons.compat.jei.category.SmeltingCategory;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.recipe.IFocusFactory;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SmelterContainerHandler implements IGuiContainerHandler<SmelterGui> {
    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(SmelterGui containerScreen, double guiMouseX, double guiMouseY) {
        List<IGuiClickableArea> areas = new ArrayList<>();
        areas.add(new IGuiClickableArea() {
            @Override
            public Rect2i getArea() {
                return new Rect2i(90, 34, 22, 15);
            }

            @Override
            public List<Component> getTooltipStrings() {
                List<Component> tooltips = new ArrayList<>();
                tooltips.add(Component.literal("Show Recipes"));
                tooltips.addAll(containerScreen.getTooltips());
                return tooltips;
            }

            @Override
            public void onClick(IFocusFactory focusFactory, IRecipesGui recipesGui) {
                recipesGui.showTypes(Lists.newArrayList(SmeltingCategory.RECIPE_TYPE));
            }
        });

        return areas;
    }
}
