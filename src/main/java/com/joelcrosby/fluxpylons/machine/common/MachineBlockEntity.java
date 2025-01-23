package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.energy.FluxEnergyStorage;
import com.joelcrosby.fluxpylons.recipe.common.BaseRecipe;
import com.joelcrosby.fluxpylons.recipe.common.RecipeInputContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public abstract class MachineBlockEntity extends BlockEntity implements MenuProvider {

    private final FluxEnergyStorage energyStorage;

    private final BlockEntityType<?> type;

    protected MachineState machineState = MachineState.IDLE;

    protected long consumedEnergy = 0;
    protected long maxEnergy = 0;

    public MachineBlockEntity(BlockEntityType<?> type,  BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.type = type;

        this.energyStorage = new FluxEnergyStorage(100000, 1000, 0);
    }

    public abstract MachineCapabilityHandler getCapabilityHandler();

    @Nullable
    public abstract BaseRecipe getRecipe(Level level, RecipeInputContainer container) ;

    @Override
    public Component getDisplayName() {
        var name = BlockEntityType.getKey(type).getPath();
        return Component.translatable("container." + FluxPylons.ID + "." + name);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
    }

    public void tick(Level level, BlockPos pos, BlockState state) {
        sendClientUpdate();

        BlockState blockstate;

        var inventory = getCapabilityHandler().items();
        var fluidInventory = getCapabilityHandler().fluids();

        if (inventory == null) return;

        var container = new RecipeInputContainer(inventory.getSlots());

        for (var i = 0; i < inventory.getInputSlots(); i++) {
            var inputStack = inventory.getStackInSlot(i);
            container.addItem(inputStack);
        }

        var energy = 20;
        var recipe = getRecipe(level, container);

        if (recipe == null || !getCapabilityHandler().canProcessInput(recipe)) {
            consumedEnergy = 0;

            if (state.getValue(BlockStateProperties.LIT) == Boolean.TRUE) {
                blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
                level.setBlock(pos, blockstate, 3);
            }

            return;
        }

        maxEnergy = recipe.energy;

        if (getCapabilityHandler().hasOutputSpaceForRecipe(recipe) && canConsumeEnergy()) {
            if (machineState == MachineState.COMPLETE) {

                // consume recipe inputs

                for (var i = 0; i < recipe.ingredients.size(); i++) {
                    var ingredient = recipe.ingredients.get(i);
                    var amount = Arrays.stream(ingredient.getItems()).findFirst().orElse(ItemStack.EMPTY).getCount();

                    inventory.extractItem(i, amount, false);
                }

                for (var i = 0; i < recipe.fluidIngredients.size(); i++) {
                    var ingredient = recipe.fluidIngredients.get(i);
                    var amount = Arrays.stream(ingredient.getStacks()).findFirst().orElse(FluidStack.EMPTY).getAmount();

                    fluidInventory.drainInput(i, amount, IFluidHandler.FluidAction.EXECUTE);
                }

                // insert recipe output into machine inventory

                for (var i = 0; i < recipe.outputItems.size(); i++) {
                    var outputCount = recipe.outputItems.get(i).count();
                    var stack = recipe.outputItems.get(i).getItemStack();
                    var newOutputStack = stack.copy();
                    var outputSlot = inventory.getAvailableOutputSlot(outputCount, stack);

                    if (outputSlot == -1) {
                        return;
                    }

                    var output = inventory.getAvailableOutputStack(outputCount, stack);

                    // Manipulating the Output slot

                    if (output.getItem() != newOutputStack.getItem() || output.getItem() == Items.AIR) {
                        if (output.getItem() == Items.AIR) { // Fix air > 1 jamming slots
                            output.setCount(1);
                        }

                        newOutputStack.setCount(recipe.outputItems.get(i).count());
                        inventory.insertItem(outputSlot, newOutputStack.copy(), false);
                    } else {
                        output.setCount(recipe.outputItems.get(i).count());
                        inventory.insertItem(outputSlot, newOutputStack.copy(), false);
                    }
                }

                machineState = MachineState.IDLE;
                consumedEnergy = 0;

                consumeEnergy(energy);
                setChanged();
            } else if (machineState == MachineState.PROCESSING) { // In progress
                consumeEnergy(energy);

                if (consumedEnergy >= recipe.energy) {
                    machineState = MachineState.COMPLETE;
                }
            } else {
                // Check if we should start processing

                if (inventory.hasOutputSpaceForRecipe(recipe)) {
                    machineState = MachineState.PROCESSING;
                    blockstate = state.setValue(BlockStateProperties.LIT, Boolean.TRUE);
                } else {
                    machineState = MachineState.IDLE;
                    blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
                }

                level.setBlock(pos, blockstate, 3);
                this.setChanged();
            }
        } else {
            // This is if we reach the maximum in the slots; or no power

            if (!canConsumeEnergy()) { // if no power
                machineState = MachineState.IDLE;
            } else {
                // zero in other cases

                machineState = MachineState.IDLE;
                consumedEnergy = 0;
            }

            blockstate = state.setValue(BlockStateProperties.LIT, Boolean.FALSE);
            level.setBlock(pos, blockstate, 3);

            this.setChanged();
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MachineBlockEntity entity) {
        entity.tick(level, pos, state);

        if (entity.getEnergyItemCapability() != null) {
            var itemEnergyStorage = entity.getEnergyItemCapability();

            if (entity.energyStorage.getEnergyStored() < entity.energyStorage.getMaxEnergyStored()) {
                entity.energyStorage.receiveEnergy(itemEnergyStorage.extractEnergy(200, false), false);
            }
        }
    }

    public void sendClientUpdate() {
        if (level == null) return;
        level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 1);
    }

    public IEnergyStorage getEnergy() {
        return energyStorage;
    }

    public void consumeEnergy(int amount) {
        var toExtract = amount;

        var itemCap = getEnergyItemCapability();
        if (itemCap != null) {
            toExtract = toExtract - itemCap.extractEnergy(toExtract, false);
        }

        if (energyStorage != null) {
            toExtract = toExtract - energyStorage.extractInternal(toExtract, false);
        }

        consumedEnergy = consumedEnergy + amount - toExtract;
    }

    @Nullable
    public IEnergyStorage getEnergyItemCapability() {
        var handler = getCapabilityHandler().items();
        var energySlot = handler.getSlots() - 1;
        var energyStack = handler.getStackInSlot(energySlot);

        return energyStack.getCapability(Capabilities.EnergyStorage.ITEM);
    }

    public boolean canConsumeEnergy() {
        return energyStorage.getEnergyStored() > 0;
    }

    public int getProgress() {
        if (maxEnergy <= 0) return 0;
        if (consumedEnergy > maxEnergy) return 100;
        return (int) Math.floor(((float)consumedEnergy / (float)maxEnergy) * 100);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag(@NotNull HolderLookup.Provider provider) {
        var compoundTag = new CompoundTag();
        this.saveAdditional(compoundTag, provider);
        return compoundTag;
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.energyStorage.setEnergyStored(compound.getInt("energy"));

        var inventory = compound.getCompound("inventory");
        var handler = getCapabilityHandler().items();
        if (handler != null) {
            handler.deserializeNBT(provider, inventory);
        }

        var fluidInventory = compound.getCompound("fluidInventory");
        var fluidHandler = getCapabilityHandler().fluids();
        if (fluidHandler != null) {
            fluidHandler.readFromNBT(fluidInventory, provider);
        }

        maxEnergy = compound.getLong("maxEnergy");
        consumedEnergy = compound.getLong("consumedEnergy");
        machineState = MachineState.values()[compound.getInt("state")];

        super.loadAdditional(compound, provider);
    }

    @Override
    protected void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        compound.putInt("energy", energyStorage.getEnergyStored());

        getCapabilityHandler().saveAdditional(compound, provider);

        compound.putLong("maxEnergy", maxEnergy);
        compound.putLong("consumedEnergy", consumedEnergy);
        compound.putInt("state", machineState.ordinal());
    }
}
