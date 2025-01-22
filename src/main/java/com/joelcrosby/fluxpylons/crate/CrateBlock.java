package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.Utility;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class CrateBlock extends BaseEntityBlock {

    public CrateBlock() {
        super(Properties.of().sound(SoundType.NETHERITE_BLOCK).strength(1.2f));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        var entity = level.getBlockEntity(pos);

        if (entity == null)
            return InteractionResult.PASS;

        if (entity instanceof CrateBlockEntity) {
            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.PLAYERS, 1, 1);

            player.openMenu((MenuProvider) entity, entity.getBlockPos());
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            var entity = world.getBlockEntity(pos);

            if (entity instanceof CrateBlockEntity) {
                Containers.dropContents(world, pos, (Container)entity);
                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        Utility.addTooltip(BuiltInRegistries.BLOCK.getKey(this).getPath(), tooltip);
    }
}
