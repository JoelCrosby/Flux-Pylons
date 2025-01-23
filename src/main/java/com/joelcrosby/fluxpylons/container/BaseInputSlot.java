package com.joelcrosby.fluxpylons.container;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.List;

public class BaseInputSlot extends SlotItemHandler {

    private final List<ItemStack> validItems;

    public BaseInputSlot(IItemHandler itemHandler, List<ItemStack> validItems, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
        this.validItems = validItems;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        for (var item : validItems) {
            if (ItemStack.isSameItem(stack, item)) {
                return true;
            }
        }

        return false;
    }
}
