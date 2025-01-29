package com.joelcrosby.fluxpylons.pylon;

import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNode;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;


public class PylonBlockEntity extends BlockEntity {
    private final PylonGraphNodeType nodeType;
    private boolean unloaded;

    protected final Set<BlockPos> connections = new CopyOnWriteArraySet<>();

    public PylonBlockEntity(BlockPos pos, BlockState state, PylonGraphNodeType nodeType) {
        super(FluxPylonsBlockEntities.PYLON.get(), pos, state);

        this.nodeType = nodeType;
    }

    public Set<BlockPos> getConnections() {
        return this.connections;
    }

    public void updateConnections(Collection<BlockPos> connections) {
        this.connections.clear();
        this.connections.addAll(connections);

        this.markDirtyClient();
    }

    public void markDirtyClient() {
        this.setChanged();

        if (this.getLevel() != null) {
            var state = this.getLevel().getBlockState(this.getBlockPos());
            this.getLevel().sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
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
            var manager = PylonNetworkManager.get(level);

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

        var manager = PylonNetworkManager.get(level);

        if (manager.getNode(worldPosition) == null) {
            var dir = getBlockState()
                    .getOptionalValue(BlockStateProperties.FACING)
                    .orElse(Direction.DOWN);

            manager.addNode(new PylonGraphNode((ServerLevel) level, worldPosition, dir, nodeType));
        }
    }

    @Override
    public final CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        var tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public final void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        this.loadAdditional(tag, provider);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);

        var connectionsTag = new ListTag();
        for (var pos : this.connections) {
            connectionsTag.add(NbtUtils.writeBlockPos(pos));
        }

        compound.put("connections", connectionsTag);
    }

    @Override
    public void loadAdditional(CompoundTag compound, HolderLookup.Provider provider) {
        var connectionsCompound = compound.getList("connections", Tag.TAG_COMPOUND);

        this.connections.clear();

        for (var i = 0; i < connectionsCompound.size(); i++) {
            var pos = NbtUtils.readBlockPos(connectionsCompound.getCompound(i), "connections");
            pos.ifPresent(this.connections::add);
        }

        super.loadAdditional(compound, provider);
    }

    public IEnergyStorage getEnergyStorage(Direction side) {
        if (side != getBlockState().getValue(BlockStateProperties.FACING)) {
            return null;
        }

        if (level != null && !level.isClientSide) {
            var node = PylonNetworkManager.get(level).getNode(this.worldPosition);

            if (node != null) {
                var network = node.getNetwork();
                if (network != null) {
                    return network.GetEnergyStorage();
                }
            }
        }

        return null;
    }
}
