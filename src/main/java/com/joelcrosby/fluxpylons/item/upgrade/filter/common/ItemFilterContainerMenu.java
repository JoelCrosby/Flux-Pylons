package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemFilterContainerMenu extends BaseFilterContainerMenu {

    @SuppressWarnings("unused")
    public ItemFilterContainerMenu(int windowId, Inventory playerInventory, Player player, RegistryFriendlyByteBuf data) {
        this(windowId, player, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
    }

    public ItemFilterContainerMenu(int windowId, Player player, ItemStack filterItem) {
        super(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU.get(), windowId, player, filterItem);
    }
}
