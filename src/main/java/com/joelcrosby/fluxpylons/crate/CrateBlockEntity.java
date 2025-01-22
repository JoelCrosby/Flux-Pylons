package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends BlockEntity implements Container, MenuProvider {

    private final static int SIZE = 54;

    public final ItemStackHandler items = new ItemStackHandler(SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            setChanged();
        }
    };

    private IItemHandlerModifiable handler;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(FluxPylonsBlockEntities.CRATE.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + FluxPylons.ID + ".crate");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int window, Inventory inventory, Player player) {
        return new CrateContainerMenu(window, player, worldPosition);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (var i = 0; i < items.getSlots(); i++) {
            var inSlot = items.getStackInSlot(i);

            if (!inSlot.isEmpty()) return false;
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.getStackInSlot(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return items.extractItem(slot, amount, false);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return items.extractItem(slot, items.getStackInSlot(slot).getCount(), true);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (var i = 0; i < this.items.getSlots(); i++)
            this.items.setStackInSlot(i, ItemStack.EMPTY);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.put("extractItems", this.items.serializeNBT(provider));
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.items.deserializeNBT(provider, compound.getCompound("extractItems"));
        super.loadAdditional(compound, provider);
    }

    private IItemHandlerModifiable createHandler() {
        return new InvWrapper(this);
    }
}
