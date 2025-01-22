package com.joelcrosby.fluxpylons.machine.common;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.WrenchItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class MachineBlock extends BaseEntityBlock {
    public MachineBlock(Properties props) {
        super(Properties.of());

        var state = this.defaultBlockState()
                .setValue(BlockStateProperties.LIT, false)
                .setValue(BlockStateProperties.FACING, Direction.NORTH);

        this.registerDefaultState(state);
    }

    @Override
    public ItemInteractionResult useItemOn(@NotNull ItemStack stack,
                                           @NotNull BlockState state,
                                           @NotNull Level level,
                                           @NotNull BlockPos pos,
                                           @NotNull Player player,
                                           @NotNull InteractionHand hand,
                                           @NotNull BlockHitResult hit) {
        var itemStack = player.getItemInHand(player.getUsedItemHand());
        var entity = level.getBlockEntity(pos);

        if (!player.isCrouching() && itemStack.getItem() instanceof WrenchItem) {
            return ItemInteractionResult.FAIL;
        }

        if (entity instanceof MachineBlockEntity machine) {
            if (!player.isCrouching() && itemStack.getCapability(Capabilities.FluidHandler.ITEM) != null) {
                return getFluidItemInteractionResult(level, player, hand, itemStack, machine);
            } else if (!level.isClientSide && player instanceof ServerPlayer serverPlayer ) {
                serverPlayer.openMenu(machine, machine.getBlockPos());
            }

            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }

        return ItemInteractionResult.SUCCESS;
    }

    private static ItemInteractionResult getFluidItemInteractionResult(Level level, Player player, InteractionHand hand, ItemStack itemStack, MachineBlockEntity entity) {
        var result = ItemInteractionResult.sidedSuccess(level.isClientSide);

        var itemCap = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        var machineCap = entity.getCapabilityHandler().fluids();

        var toDrain = machineCap.getTankCapacity(0) - machineCap.getFluidInTank(0).getAmount();
        var simulatedDrain = itemCap.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);

        if (simulatedDrain.getAmount() == itemCap.getFluidInTank(0).getAmount()) {
            var drained = itemCap.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
            machineCap.fill(drained, IFluidHandler.FluidAction.EXECUTE);

            var container = itemCap.getContainer();

            if (itemStack.getCount() == 1) {
                player.setItemInHand(hand, container);
            } else if (itemStack.getCount() > 1 && player.getInventory().add(container)) {
                itemStack.shrink(1);
            } else {
                player.drop(container, false, true);
                itemStack.shrink(1);
            }

            player.getInventory().setChanged();
        } else {
            var drained = itemCap.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
            machineCap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
        }

        return result;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var entity = world.getBlockEntity(pos);

            if (entity instanceof MachineBlockEntity machine) {
                var handler = machine.getCapabilityHandler().items();
                var container = new SimpleContainer(handler.getOutputSlots() + handler.getInputSlots());

                for (var i = 0; i < handler.getInputSlots(); i++) {
                    var stack = handler.getInputItemStack(i);
                    container.addItem(stack);
                }

                for (var i = 0; i < handler.getOutputSlots(); i++) {
                    var stack = handler.getOutputItemStack(i);
                    container.addItem(stack);
                }

                Containers.dropContents(world, pos, container);
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.LIT);
        builder.add(BlockStateProperties.FACING);
    }

    @Nullable
    protected static <T extends BlockEntity> BlockEntityTicker<T> createTicker(Level level, BlockEntityType<T> passedBlockEntity, BlockEntityType<? extends MachineBlockEntity> entityType) {
        return level.isClientSide ? null : createTickerHelper(passedBlockEntity, entityType, MachineBlockEntity::serverTick);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        Utility.addTooltip(BuiltInRegistries.BLOCK.getKey(this).getPath(), tooltip);
    }
}
