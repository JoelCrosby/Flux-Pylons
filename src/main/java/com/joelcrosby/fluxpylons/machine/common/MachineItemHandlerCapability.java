package com.joelcrosby.fluxpylons.machine.common;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;

public class MachineItemHandlerCapability implements IItemHandler {

    private final IItemHandler handler;
    private final SlotRange inputSlotRange;

    public MachineItemHandlerCapability(IItemHandler itemHandler, SlotRange inputSlotRange) {
        this.handler = itemHandler;
        this.inputSlotRange = inputSlotRange;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (inputSlotRange.contains(slot)) {
            return ItemStack.EMPTY;
        }

        return handler.extractItem(slot, amount, simulate);
                
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack itemStack) {
        return handler.isItemValid(slot, itemStack);
    }
}
