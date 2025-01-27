package com.joelcrosby.fluxpylons;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.ArrayList;
import java.util.Set;

public class FluxPylonsLootTables extends BlockLootSubProvider {
    public FluxPylonsLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.VANILLA_SET, registries);
    }

    @Override
    protected void generate() {
        var blocks = FluxPylonsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList();

        for (var block : blocks) {
            dropSelf(block);
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return new ArrayList<>(FluxPylonsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList());
    }
}
