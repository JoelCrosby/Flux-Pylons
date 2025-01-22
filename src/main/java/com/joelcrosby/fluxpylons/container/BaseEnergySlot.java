package com.joelcrosby.fluxpylons.container;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class BaseEnergySlot extends SlotItemHandler {
    public BaseEnergySlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(@Nullable ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        return stack.getCapability(Capabilities.EnergyStorage.ITEM) != null;
    }
}
