package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class TagFilterItem extends BaseFilterItem {

    @Override
    protected int getSlots() {
        return 1;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        var stack = player.getItemInHand(interactionHand);

        if (level.isClientSide()) return new InteractionResultHolder<>(InteractionResult.PASS, stack);

        openGui(player, stack);

        return new InteractionResultHolder<>(InteractionResult.PASS, stack);
    }

    public void openGui(Player player, ItemStack stack) {
        var containerName = Component.translatable("container." + FluxPylons.ID + "." + BuiltInRegistries.ITEM.getKey(this).getPath());

        player.openMenu(
                new SimpleMenuProvider((windowId, playerInventory, playerEntity) ->
                        new TagFilterContainerMenu(windowId, player, stack), containerName),
                (buffer -> ItemStack.STREAM_CODEC.encode(buffer, stack))
        );
    }

    @Override
    protected boolean supportsNbtMatch() {
        return false;
    }
}
