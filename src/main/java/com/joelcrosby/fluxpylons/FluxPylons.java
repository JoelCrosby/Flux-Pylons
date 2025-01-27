package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.setup.Client;

import com.joelcrosby.fluxpylons.setup.Common;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;

@Mod(FluxPylons.ID)
public class FluxPylons
{
    public static final String ID = "fluxpylons";

    public FluxPylons(IEventBus bus)
    {
        FluxPylonsBlocks.BLOCKS.register(bus);
        FluxPylonsBlockEntities.BLOCK_ENTITIES_REGISTRY.register(bus);
        FluxPylonsItems.ITEM_REGISTRY.register(bus);
        FluxPylonsContainerMenus.CONTAINERS.register(bus);
        FluxPylonsDataComponents.DATA_COMPONENT_TYPE_DEFERRED_REGISTER.register(bus);

        FluxPylonsRecipes.RECIPE_SERIALIZERS.register(bus);
        FluxPylonsRecipes.FluxPylonsRecipeTypes.RECIPE_TYPES_REGISTRY.register(bus);

        Common.CREATIVE_MODE_TABS.register(bus);

        bus.addListener(PacketHandler::register);
        bus.addListener(FluxPylonsCapabilities::registerCapabilities);
        bus.addListener(FluxPylonsDataGenerators::gatherData);

        if (FMLLoader.getDist().isClient()) {
            bus.addListener(Client::setup);
        }
    }
}
