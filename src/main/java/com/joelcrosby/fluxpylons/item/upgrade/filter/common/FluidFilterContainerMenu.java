package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.util.FluidHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

public class FluidFilterContainerMenu extends BaseFilterContainerMenu {

    @SuppressWarnings("unused")
    public FluidFilterContainerMenu(int windowId, Inventory playerInventory, Player player, RegistryFriendlyByteBuf data) {
        this(windowId, player, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
    }

    public FluidFilterContainerMenu(int windowId, Player player, ItemStack filterItem) {
        super(FluxPylonsContainerMenus.UPGRADE_FLUID_FILTER_CONTAINER_MENU.get(), windowId, player, filterItem);
    }

    @Override
    protected void addOwnSlots() {
        var off = 18 * 2;
        var y = 18;

        var slot = -1;
        var rows = FluxPylonsContainerMenus.BaseFilterContainerSlots / 2;

        for (var i = 0; i < this.itemStackHandler.getSlots() / rows; i++) {
            for (var j = 0; j < rows; j++) {
                slot++;
                this.addSlot(new FluidFilterSlotHandler(this.itemStackHandler, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        var stack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var currentStack = slot.getItem().copy();

            if (!FluidHelper.isFluidHandler(currentStack)) {
                return ItemStack.EMPTY;
            }

            if (ItemStack.isSameItemSameComponents(currentStack, filterItem)) {
                return ItemStack.EMPTY;
            }

            currentStack.setCount(1);

            if (index >= getSlotCount()) {
                for (int i = 0; i < getSlotCount(); i++) {
                    var slotStack = this.slots.get(i).getItem();
                    var fluidStack = FluidHelper.getFromStack(slotStack, true).getValue();
                    var fluidInItem = FluidUtil.getFluidContained(currentStack);

                    if (fluidInItem.isPresent() && FluidStack.isSameFluid(fluidStack, fluidInItem.get())) {
                        return ItemStack.EMPTY;
                    }
                }

                if (!this.moveItemStackTo(currentStack, 0, getSlotCount(), false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}
