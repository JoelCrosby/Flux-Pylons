package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Optional;

public abstract class MachineCapabilityHandler {

    @Nullable
    public abstract MachineItemStackHandler items();
    public abstract Optional<MachineItemStackHandler> itemHandler();

    @Nullable
    public abstract MachineFluidHandler fluids();
    public abstract Optional<MachineFluidHandler> fluidHandler();

    public boolean hasOutputSpaceForRecipe(BaseRecipe recipe)
    {
        var items = true;
        var fluids = true;

        if (items() != null) {
            fluids = items().hasOutputSpaceForRecipe(recipe);
        }

        if (fluids() != null) {
            fluids = fluids().hasOutputSpaceForRecipe(recipe);
        }

        return items &&  fluids;
    }

    public boolean canProcessInput(BaseRecipe recipe) {
        var items = true;
        var fluids = true;

        if (items() != null) {
            fluids = items().canProcessInput(recipe);
        }

        if (fluids() != null) {
            fluids = fluids().canProcessInput(recipe);
        }

        return items &&  fluids;
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
