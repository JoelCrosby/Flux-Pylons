package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;

import javax.annotation.Nullable;
import java.util.Objects;

public abstract class MachineCapabilityHandler {

    @Nullable
    public abstract MachineItemStackHandler items();

    @Nullable
    public abstract MachineFluidHandler fluids();

    public boolean hasOutputSpaceForRecipe(BaseRecipe recipe)
    {
        var canProcessItems = true;
        var canProcessFluids = true;

        var items = items();
        var fluids = fluids();

        if (items != null) {
            canProcessItems = items.hasOutputSpaceForRecipe(recipe);
        }

        if (fluids != null) {
            canProcessFluids = fluids.hasOutputSpaceForRecipe(recipe);
        }

        return canProcessItems && canProcessFluids;
    }

    public boolean canProcessInput(BaseRecipe recipe) {
        var canProcessItems = true;
        var canProcessFluids = true;

        var items = items();
        var fluids = fluids();

        if (items != null) {
            canProcessItems = items.canProcessInput(recipe);
        }

        if (fluids != null) {
            canProcessFluids = fluids.canProcessInput(recipe);
        }

        return canProcessItems && canProcessFluids;
    }

    public IItemHandler getItemHandlerCapability() {
        return Objects.requireNonNull(items()).getItemHandlerCapability();
    }

    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        var handler = items();
        if (handler != null) {
            var inventory = ((INBTSerializable<CompoundTag>)handler).serializeNBT(provider);
            compound.put("inventory", inventory);
        }

        var fluidHandler = fluids();
        if (fluidHandler != null) {
            var fluidTag = new CompoundTag();
            fluidHandler.writeToNBT(fluidTag, provider);
            compound.put("fluidInventory", fluidTag);
        }
    }
}
