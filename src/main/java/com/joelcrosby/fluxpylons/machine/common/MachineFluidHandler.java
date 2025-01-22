package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineFluidHandler implements IFluidHandler {

    public List<FluidTank> inputTanks;
    public List<FluidTank> outputTanks;

    public MachineFluidHandler(int inputTanks, int outputTanks) {
        this.inputTanks = setupTanks(inputTanks);
        this.outputTanks = setupTanks(outputTanks);
    }

    private List<FluidTank> setupTanks(int count) {
        var tanks = new ArrayList<FluidTank>(count);

        for (var i = 0; i < count; i++) {
            tanks.add(new FluidTank(getTankCapacity(i)));
        }

        return tanks;
    }

    public boolean hasOutputSpaceForRecipe(BaseRecipe recipe)
    {
        if (outputTanks.isEmpty()) return true;

        for (var i = 0; i < recipe.getOutputItems().size(); i++) {
            var output = recipe.outputFluids.get(i);
            var amount = output.getFluidStack().getAmount();

            if (i < outputTanks.size()) {
                var tank = outputTanks.get(i);

                if (tank.getFluidAmount() > (tank.getCapacity() - amount)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean canProcessInput(BaseRecipe recipe)
    {
        if (inputTanks.isEmpty()) return true;

        for (var i = 0; i < recipe.fluidIngredients.size(); i++) {
            var output = Arrays.stream(recipe.fluidIngredients.get(i).getStacks()).findFirst().orElse(FluidStack.EMPTY);
            var amount = output.getAmount();
            var tank = inputTanks.get(i);

            if (tank.getFluidAmount() < amount) {
                return false;
            }

        }

        return true;
    }

    @Override
    public int getTanks() {
        return inputTanks.size() + outputTanks.size();
    }

    @NotNull
    @Override
    public FluidStack getFluidInTank(int tank) {

        if (tank < inputTanks.size()) {
            return inputTanks.get(tank).getFluidInTank(0);
        }

        if (tank >= inputTanks.size()) {
            var index = tank - inputTanks.size();

            if (index < outputTanks.size()) {
                return outputTanks.get(index).getFluidInTank(0);
            }
        }

        return FluidStack.EMPTY;
    }

    public void readFromNBT(CompoundTag compoundTag, HolderLookup.Provider provider) {
        for (var i = 0; i < inputTanks.size(); i++) {
            var tankTag = compoundTag.getCompound("input-tank-" + i);
            inputTanks.get(i).readFromNBT(provider, tankTag);
        }

        for (var i = 0; i < outputTanks.size(); i++) {
            var tankTag = compoundTag.getCompound("output-tank-" + i);
            outputTanks.get(i).readFromNBT(provider, tankTag);
        }
    }

    public void writeToNBT(CompoundTag compoundTag, HolderLookup.Provider provider) {
        for (var i = 0; i < inputTanks.size(); i++) {
            var tankTag = new CompoundTag();
            inputTanks.get(i).writeToNBT(provider, tankTag);
            compoundTag.put("input-tank-" + i, tankTag);
        }

        for (var i = 0; i < outputTanks.size(); i++) {
            var tankTag = new CompoundTag();
            outputTanks.get(i).writeToNBT(provider, tankTag);
            compoundTag.put("output-tank-" + i, tankTag);
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        return 1000 * 10;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return true;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return inputTanks.stream().findFirst().map(tank -> tank.fill(resource, action)).orElse(0);
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return inputTanks.stream().findFirst().map(tank -> tank.drain(resource, action)).orElse(FluidStack.EMPTY);
    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return inputTanks.stream().findFirst().map(tank -> tank.drain(maxDrain, action)).orElse(FluidStack.EMPTY);
    }

    @NotNull
    public FluidStack drainInput(int tank, int maxDrain, FluidAction action) {
        return inputTanks.get(tank).drain(maxDrain, action);
    }

    @NotNull
    public FluidStack drainOutput(int tank, int maxDrain, FluidAction action) {
        return outputTanks.get(tank).drain(maxDrain, action);
    }
}
