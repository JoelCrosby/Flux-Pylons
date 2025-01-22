package com.joelcrosby.fluxpylons.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.tuple.Pair;

public class FluidHelper {
    public static boolean isFluidHandler(ItemStack stack) {
        return !getFromStack(stack, true).getKey().isEmpty();
    }

    public static Pair<ItemStack, FluidStack> getFromStack(ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }

        if (stack.getCount() > 1) {
            stack = new ItemStack(stack.getItem(), 1);
        }

        var handler = stack.getCapability(Capabilities.FluidHandler.ITEM, null);
        if (handler == null) {
            return Pair.of(ItemStack.EMPTY, FluidStack.EMPTY);
        }

        var result = handler.drain(1000, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

        return Pair.of(handler.getContainer(), result);
    }
}
