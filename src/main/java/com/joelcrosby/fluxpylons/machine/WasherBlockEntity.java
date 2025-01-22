package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineCapabilityHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineFluidHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.WasherRecipe;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WasherBlockEntity extends MachineBlockEntity {

    private final MachineCapabilityHandler capabilityHandler = new MachineCapabilityHandler() {
        private final MachineItemStackHandler inventory = new MachineItemStackHandler(1, 2, true);

        private final MachineFluidHandler fluidInventory = new MachineFluidHandler(1, 0) {
            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                var name = BuiltInRegistries.FLUID.getKey(stack.getFluid()).getPath();
                return name.equals("water");
            }
        };

        @Override
        public MachineItemStackHandler items() {
            return inventory;
        }

        @Override
        public Optional<MachineItemStackHandler> itemHandler() {
            return Optional.of(inventory);
        }

        @Override
        public MachineFluidHandler fluids() {
            return fluidInventory;
        }

        @Override
        public Optional<MachineFluidHandler> fluidHandler() {
            return Optional.of(fluidInventory);
        }
    };

    public WasherBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.WASHER.get(), pos, state);
    }

    @Override
    public MachineCapabilityHandler getCapabilityHandler() {
        return capabilityHandler;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new WasherContainerMenu(window, player, worldPosition);
    }

    @Override
    public WasherRecipe getRecipe(Level level, RecipeInputContainer input) {
        return WasherRecipe.getRecipe(level, input);
    }

    public FluidStack getFluidStack() {
        return capabilityHandler.fluids().getFluidInTank(0);
    }

    public int getFluidTankCapacity() {
        return capabilityHandler.fluids().getTankCapacity(0);
    }
}
