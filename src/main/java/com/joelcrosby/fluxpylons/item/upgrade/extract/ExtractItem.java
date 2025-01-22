package com.joelcrosby.fluxpylons.item.upgrade.extract;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphDestinationType;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.ItemHandlerHelper;


public class ExtractItem extends BaseFilterItem {

    @Override
    protected int getSlots() {
        return FluxPylonsContainerMenus.BaseFilterContainerSlots;
    }

    @Override
    protected boolean supportsInteractionSide() {
        return true;
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
                        new ItemFilterContainerMenu(windowId, player, stack), containerName),
                (buffer -> ItemStack.STREAM_CODEC.encode(buffer, stack))
        );
    }

    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {
        var level = node.getLevel();
        var source = level.getBlockEntity(node.getPos().relative(dir));

        if (source == null) return;

        var isDenyList = BaseFilterItem.getIsDenyList(itemStack);
        var matchNbt = BaseFilterItem.getMatchNbt(itemStack);
        var inventory = BaseFilterItem.getInventory(itemStack);
        var interactionDir = BaseFilterItem.getInteractionSide(itemStack);

        var handlerDir = interactionDir == null ? dir.getOpposite() : interactionDir;

        var capCache = BlockCapabilityCache.create(
                Capabilities.ItemHandler.BLOCK,
                level,
                source.getBlockPos(),
                handlerDir
        );

        var itemHandler = capCache.getCapability();

        if (itemHandler == null) return;

        var rate = nodeType.getItemTransferRate();

        var iterations = node.getNodeType() == GraphNodeType.ADVANCED ? 4 : 1;

        for (var j = 0; j < iterations; j++) {

            Slots:
            for (var i = 0; i < itemHandler.getSlots(); i++) {
                var slot = itemHandler.getStackInSlot(i);
                if (slot.isEmpty()) {
                    continue;
                }

                var matchesFilter = Utility.matchesFilterInventory(inventory, slot, matchNbt);
                if (isDenyList == matchesFilter) {
                    continue;
                }

                var simulatedExtract = itemHandler.extractItem(i, rate, true);
                if (simulatedExtract.isEmpty()) {
                    continue;
                }

                var destinations = node.getNetwork()
                        .getRelativeDestinations(GraphDestinationType.ITEMS, source.getBlockPos());

                for (var destination : destinations) {
                    if (!destination.canInsert()) continue;

                    var destinationEntity = destination.getConnectedBlockEntity();
                    if (destinationEntity == null) continue;

                    var destinationPos = destination.getConnectedBlockEntity().getBlockPos();

                    if (destinationPos == source.getBlockPos()) {
                        throw new RuntimeException("destination cannot be the same as source");
                    }

                    var incomingDirection = destination.incomingDirection().getOpposite();

                    var destCapCache = BlockCapabilityCache.create(
                            Capabilities.ItemHandler.BLOCK,
                            level,
                            destinationPos,
                            incomingDirection
                    );

                    var destinationHandler = destCapCache.getCapability();

                    if (destinationHandler == null) continue;

                    var upgradeManager = destination.getConnectedUpgradeManager();
                    if (!upgradeManager.IsValidDestination(simulatedExtract)) {
                        continue;
                    }

                    var remainder = ItemHandlerHelper.insertItem(destinationHandler, simulatedExtract, true).getCount();
                    var amountToExtract = simulatedExtract.getCount() - remainder;

                    if (amountToExtract == 0) continue;

                    var extracted = itemHandler.extractItem(i, amountToExtract, false);
                    ItemHandlerHelper.insertItem(destinationHandler, extracted, false);

                    break Slots;
                }
            }
        }
    }
}
