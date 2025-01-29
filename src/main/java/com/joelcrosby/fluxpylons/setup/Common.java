package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.FluxPylons;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.joelcrosby.fluxpylons.FluxPylonsItems.ITEM_REGISTRY;
import static com.joelcrosby.fluxpylons.FluxPylonsItems.WRENCH;

public class Common {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, FluxPylons.ID);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_MODE_TABS.register(FluxPylons.ID, () -> CreativeModeTab.builder()
            .title(Component.literal("Flux Pylons"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> new ItemStack(WRENCH))
            .displayItems((parameters, output) -> ITEM_REGISTRY.getEntries().forEach(e -> {
                var item = e.get();
                output.accept(item);
            })).build());
}
