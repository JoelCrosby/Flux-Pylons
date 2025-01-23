package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class MachineItemStackHandler extends ItemStackHandler {
    private final int inputSlots;
    private final int outputSlots;
    private final boolean hasEnergySlot;

    private final SlotRange inputSlotRange;
    private final SlotRange outputSlotRange;

    private final IItemHandler capabilityHandler;

    public MachineItemStackHandler(int inputSlots, int outputSlots, boolean hasEnergySlot) {
        super(inputSlots + outputSlots + (hasEnergySlot ? 1 : 0));

        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.hasEnergySlot = hasEnergySlot;
        this.inputSlotRange = calculateInputSlots();
        this.outputSlotRange = calculateOutputSlots();

        this.capabilityHandler = new MachineItemHandlerCapability(this, inputSlotRange);
    }

    private SlotRange calculateInputSlots() {
        return new SlotRange(0, this.inputSlots - 1);
    }

    private SlotRange calculateOutputSlots() {
        var high = this.inputSlots + this.outputSlots - 1;

        return new SlotRange(this.inputSlots, high);
    }

    public int getInputSlots() {
        return this.inputSlots;
    }

    public int getOutputSlots() {
        return this.outputSlots;
    }

    public int getOutputSlot(int i) {
        return this.outputSlotRange.values()[i];
    }

    public int getInputSlot(int i) {
        return this.inputSlotRange.values()[i];
    }

    public ItemStack getOutputItemStack(int i) {
        var slot = getOutputSlot(i);
        return this.stacks.get(slot);
    }

    public ItemStack getInputItemStack(int i) {
        var slot = getInputSlot(i);
        return this.stacks.get(slot);
    }

    public boolean canProcessInput(BaseRecipe recipe)
    {
        for (var i = 0; i < recipe.ingredients.size(); i++) {
            var output = Arrays.stream(recipe.ingredients.get(i).getItems()).findFirst().orElse(ItemStack.EMPTY);
            var amount = output.getCount();
            var stack = getInputItemStack(i);

            if (stack.getCount() < amount) {
                return false;
            }
        }

        return true;
    }

    public IItemHandler getItemHandlerCapability() {
        return this.capabilityHandler;
    }

    @Nonnull
    public ItemStack[] getOutputItemStacks()
    {
        var stacks = new ItemStack[outputSlots];

        for (var i = 0; i < outputSlots; i++) {
            stacks[i] = this.stacks.get(getOutputSlot(i));
        }

        return stacks;
    }

    public int getAvailableOutputSlot(int amount, ItemStack itemStack)
    {
        var i = 0;

        for (var stack : getOutputItemStacks()) {
            var canInsert = stack.isEmpty() || ItemStack.isSameItemSameComponents(stack, itemStack);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);

            if (canInsert && stackHasSpace) {
                return getOutputSlot(i);
            }

            i++;
        }

        return -1;
    }

    @Nullable
    public ItemStack getAvailableOutputStack(int amount, ItemStack itemStack)
    {
        for (var stack : getOutputItemStacks()) {
            var canInsert = stack.isEmpty() || ItemStack.isSameItemSameComponents(stack, itemStack);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);

            if (canInsert && stackHasSpace) {
                return stack.copy();
            }
        }

        return null;
    }

    public boolean hasSpaceInOutput(int amount)
    {
        for (var stack : getOutputItemStacks()) {
            if (stack.getCount() > (stack.getMaxStackSize() - amount)) {
                return false;
            }
        }

        return true;
    }

    public boolean hasOutputSpaceForRecipe(BaseRecipe recipe)
    {
        for (var i = 0; i < recipe.outputItems.size(); i++) {
            var output = recipe.outputItems.get(i).getItemStack();
            var amount = output.getCount();
            var stack = getOutputItemStack(i);

            var canInsert = stack.isEmpty() || ItemStack.isSameItemSameComponents(stack, output);
            var stackHasSpace = stack.getCount() <= (stack.getMaxStackSize() - amount);

            if (!canInsert || !stackHasSpace) {
                return false;
            }
        }

        return true;
    }
}
