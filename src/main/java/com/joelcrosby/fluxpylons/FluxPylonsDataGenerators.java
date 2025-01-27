package com.joelcrosby.fluxpylons;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;

public class FluxPylonsDataGenerators {

    public static void gatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();
        var existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(
                event.includeServer(),
                new FluxPylonsBlockTagsProvider(output, lookupProvider, existingFileHelper)
        );

        generator.addProvider(
                event.includeServer(),
                new LootTableProvider(
                    output,
                    Collections.emptySet(),
                    List.of(new LootTableProvider.SubProviderEntry(FluxPylonsLootTables::new, LootContextParamSets.BLOCK)),
                    lookupProvider
                )
        );
    }
}
