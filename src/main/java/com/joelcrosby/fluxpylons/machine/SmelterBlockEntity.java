package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlockEntity;
import com.joelcrosby.fluxpylons.machine.common.MachineCapabilityHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineFluidHandler;
import com.joelcrosby.fluxpylons.machine.common.MachineItemStackHandler;
import com.joelcrosby.fluxpylons.recipe.SmelterRecipe;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SmelterBlockEntity extends MachineBlockEntity {

    private final MachineCapabilityHandler capabilityHandler = new MachineCapabilityHandler() {
        private final MachineItemStackHandler inventory = new MachineItemStackHandler(6, 2, true);

        @Override
        public MachineItemStackHandler items() {
            return inventory;
        }

        @Override
        public Optional<MachineItemStackHandler> itemHandler() {
            return Optional.of(inventory);
        }

        @Nullable
        @Override
        public MachineFluidHandler fluids() {
            return null;
        }

        @Override
        public Optional<MachineFluidHandler> fluidHandler() {
            return Optional.empty();
        }
    };

    public SmelterBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.SMELTER.get(), pos, state);
    }

    @Nullable
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new SmelterContainerMenu(window, player, worldPosition);
    }

    @Override
    public MachineCapabilityHandler getCapabilityHandler() {
        return capabilityHandler;
    }

    @Override
    public BaseRecipe getRecipe(Level level, RecipeInputContainer container) {
        return SmelterRecipe.getRecipe(level, container);
    }
}
