package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylonsDataComponents;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ComponentItemHandler;
import org.jetbrains.annotations.NotNull;

public class ItemFilterStackHandler extends ComponentItemHandler  {
    public final ItemStack stack;

    public ItemFilterStackHandler(int size, ItemStack itemStack) {
        super(itemStack, FluxPylonsDataComponents.INVENTORY.get(), size);
        this.stack = itemStack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }


    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        for (var i = 0; i < getSlots(); i++) {
            var slotStack = getStackInSlot(i);

            if (slotStack.isEmpty()) continue;

            if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                return false;
            }
        }

        return true;
    }
}
