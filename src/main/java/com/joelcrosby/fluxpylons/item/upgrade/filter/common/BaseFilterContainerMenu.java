package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseContainerMenu;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;

public abstract class BaseFilterContainerMenu extends BaseContainerMenu {
    protected final IItemHandler itemStackHandler;

    public final ItemStack filterItem;

    @SuppressWarnings("unused")
    public BaseFilterContainerMenu(MenuType<?> menuType, int windowId, Inventory playerInventory, Player player, RegistryFriendlyByteBuf data) {
        this(menuType, windowId, player, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
    }

    public BaseFilterContainerMenu(MenuType<?> menuType, int windowId, Player player, ItemStack filterItem) {
        super(menuType, windowId, player);

        this.itemStackHandler = BaseFilterItem.getInventory(filterItem);
        this.filterItem = filterItem;

        this.addOwnSlots();
        this.addPlayerInventory();
    }

    @Override
    protected int getSlotCount() {
        return FluxPylonsContainerMenus.BaseFilterContainerSlots;
    }

    @Override
    protected Pair<Integer, Integer> getPlayerInventoryPosition() {
        return Pair.of(8, 71);
    }

    protected void addOwnSlots() {
        var off = 18 * 2;
        var y = 18;

        var slot = -1;
        var rowSlots = FluxPylonsContainerMenus.BaseFilterContainerSlots / 2;

        for (var i = 0; i < this.itemStackHandler.getSlots() / rowSlots; i++) {
            for (var j = 0; j < rowSlots; j++) {
                slot++;
                this.addSlot(new FilterSlotHandler(this.itemStackHandler, slot, 8 + off + j * 18, y + i * 18));
            }
        }
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        if (slotId >= 0 && slotId < getSlotCount()) {
            return;
        }

        super.clicked(slotId, dragType, clickType, player);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        var stack = ItemStack.EMPTY;
        var slot = this.slots.get(index);

        if (slot.hasItem()) {
            var currentStack = slot.getItem().copy();

            if (ItemStack.isSameItemSameComponents(currentStack, filterItem)) {
                return ItemStack.EMPTY;
            }

            currentStack.setCount(1);

            // Only do this if we click from the players inventory
            if (index >= getSlotCount()) {
                for (int i = 0; i < getSlotCount(); i++) { // Prevents the same item from going in there more than once.
                    if (ItemStack.isSameItem(this.slots.get(i).getItem(), currentStack)) // Don't limit tags
                        return ItemStack.EMPTY;
                }
                if (!this.moveItemStackTo(currentStack, 0, getSlotCount(), false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return stack;
    }
}
