package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.energy.FluxEnergyStorage;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineCapabilityHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineFluidHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class BoilerBlockEntity extends MachineBlockEntity {

    private int temperature;

    private final MachineCapabilityHandler capabilityHandler = new MachineCapabilityHandler() {
        private final MachineItemStackHandler inventory = new MachineItemStackHandler(3, 0, false);

        @Override
        public MachineItemStackHandler items() {
            return inventory;
        }

        private final MachineFluidHandler fluidInventory = new MachineFluidHandler(1, 0) {
            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                var name = BuiltInRegistries.FLUID.getKey(stack.getFluid()).getPath();
                return name.equals("water");
            }
        };

        @Override
        public MachineFluidHandler fluids() {
            return fluidInventory;
        }
    };

    public BoilerBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.BOILER.get(), pos, state);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new BoilerContainerMenu(window, player, worldPosition);
    }

    @Override
    public MachineCapabilityHandler getCapabilityHandler() {
        return capabilityHandler;
    }

    @Override
    public BaseRecipe getRecipe(Level level, RecipeInputContainer container) {
        return null;
    }

    @Override
    protected Optional<FluxEnergyStorage> getEnergyStorage() {
        return Optional.empty();
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        super.tick(level, pos, state);

        temperature = temperature + 1;
    }

    public int getTemperature() { return temperature; }
}
