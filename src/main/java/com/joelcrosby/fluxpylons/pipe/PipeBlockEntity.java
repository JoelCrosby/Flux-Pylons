package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class PipeBlockEntity extends BlockEntity implements IPipeConnectable {
    private boolean unloaded;
    private final PipeType pipeType;
    public BlockState cover;

    public PipeBlockEntity(BlockPos pos, BlockState state, PipeType pipeType) {
        super(pipeType.getEntityType(), pos, state);

        this.pipeType = pipeType;
    }

    public PipeType getPipeType() {
        return this.pipeType;
    }

    public PipeUpgradeManager getUpgradeManager(Direction dir) {
        var node = NetworkManager.get(level).getNode(worldPosition);

        if (node == null) {
            throw new RuntimeException("PipeBlockEntity has node attached node");
        }

        return node.getUpgradeManager(dir);
    }

    public ConnectionType getConnectionType(BlockPos pipePos, Direction direction) {
        if (this.level == null) {
            return ConnectionType.DISCONNECTED;
        }

        var state = this.level.getBlockState(pipePos.relative(direction));

        if (!(state.getBlock() instanceof PipeBlock)) {
            return ConnectionType.DISCONNECTED;
        }

        if (state.getValue(PipeBlock.DIRECTIONS.get(direction.getOpposite())) == ConnectionType.BLOCKED)
            return ConnectionType.BLOCKED;

        return ConnectionType.CONNECTED;
    }

    @SuppressWarnings("unused")
    public IEnergyStorage getEnergyStorage(Direction direction) {
        if (level != null && !level.isClientSide) {
            var node = NetworkManager.get(level).getNode(this.worldPosition);

            if (node != null) {
                var network = node.getNetwork();
                if (network != null) {
                    return network.GetEnergyStorage();
                }
            }
        }
        return null;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unloaded = true;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (level != null && !level.isClientSide && !unloaded) {
            var manager = NetworkManager.get(level);

            var pipe = manager.getNode(worldPosition);

            if (pipe != null)
                manager.removeNode(worldPosition);
        }
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();

        if (level == null || level.isClientSide) {
            return;
        }

        var manager = NetworkManager.get(level);

        if (manager.getNode(worldPosition) == null) {
            manager.addNode(new GraphNode((ServerLevel) level, worldPosition, pipeType.getNodeType()));
        }
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    @Override
    public final void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        this.loadWithComponents(tag, provider);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        this.loadWithComponents(pkt.getTag(), provider);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        if (this.cover != null)
            compound.put("cover", NbtUtils.writeBlockState(this.cover));
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        this.cover = compound.contains("cover") ? NbtUtils.readBlockState(this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup(), compound.getCompound("cover")) : null;
        super.loadAdditional(compound, provider);
    }

    public void removeCover(Player player, InteractionHand hand) {
        if (this.level != null && this.level.isClientSide){
            return;
        }

        var drops = Block.getDrops(this.cover, (ServerLevel) this.level, this.worldPosition, null, player, player.getItemInHand(hand));

        for (var drop : drops) {
            Containers.dropItemStack(this.level, this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), drop);
        }

        this.cover = null;
    }
}
