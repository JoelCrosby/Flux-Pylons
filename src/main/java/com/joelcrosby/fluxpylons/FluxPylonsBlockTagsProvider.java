package com.joelcrosby.fluxpylons;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;


public class FluxPylonsBlockTagsProvider extends BlockTagsProvider {
    public FluxPylonsBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, FluxPylons.ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "FluxPylons Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        var blocks = FluxPylonsBlocks.BLOCKS.getEntries().stream().map(DeferredHolder::getKey).toList();

        tag(BlockTags.MINEABLE_WITH_PICKAXE).addAll(blocks);
    }
}