package com.joelcrosby.fluxpylons;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FluxPylonsLootTables extends VanillaBlockLoot {
    public FluxPylonsLootTables(HolderLookup.Provider registries) {
        super(registries);
    }

    @Override
    protected void generate() {
        var blocks = FluxPylonsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::get).toList();

        for (var block : blocks) {
            dropSelf(block);
        }
    }
}
