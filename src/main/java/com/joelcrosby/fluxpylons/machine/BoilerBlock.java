package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.machine.common.MachineBlock;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BoilerBlock extends MachineBlock {
    public static final MapCodec<BoilerBlock> CODEC = BlockBehaviour.simpleCodec((p) -> new BoilerBlock());

    public BoilerBlock() {
        super(Properties.of().sound(SoundType.NETHERITE_BLOCK).strength(1.2f));
    }

    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoilerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide ? null : createTicker(level, type, FluxPylonsBlockEntities.BOILER.get());
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return BoilerBlock.CODEC;
    }
}
