package com.joelcrosby.fluxpylons.setup;

import com.joelcrosby.fluxpylons.ClientEvents;
import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsBlockEntities;
import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.crate.CrateGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.BasicFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterGui;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterGui;
import com.joelcrosby.fluxpylons.machine.BoilerGui;
import com.joelcrosby.fluxpylons.machine.SmelterGui;
import com.joelcrosby.fluxpylons.machine.WasherGui;
import com.joelcrosby.fluxpylons.pipe.PipeRenderer;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeGui;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.client.event.*;

@EventBusSubscriber(modid = FluxPylons.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public final class Client {

    @SuppressWarnings("unused")
    public static void setup(final FMLClientSetupEvent event) {
        NeoForge.EVENT_BUS.register(ClientEvents.class);

//        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.BASIC_PIPE.get(), RenderType.cutout());
//        ItemBlockRenderTypes.setRenderLayer(FluxPylonsBlocks.ADV_PIPE.get(), RenderType.cutout());

        BlockEntityRenderers.register(FluxPylonsBlockEntities.ADV_PIPE.get(), PipeRenderer::new);
        BlockEntityRenderers.register(FluxPylonsBlockEntities.BASIC_PIPE.get(), PipeRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(FluxPylonsContainerMenus.CRATE_CONTAINER_MENU.get(), CrateGui::new);
        event.register(FluxPylonsContainerMenus.PIPE_UPGRADE_CONTAINER_MENU.get(), PipeUpgradeGui::new);
        event.register(FluxPylonsContainerMenus.UPGRADE_FILTER_CONTAINER_MENU.get(), BasicFilterGui::new);
        event.register(FluxPylonsContainerMenus.UPGRADE_FLUID_FILTER_CONTAINER_MENU.get(), FluidFilterGui::new);
        event.register(FluxPylonsContainerMenus.UPGRADE_TAG_FILTER_CONTAINER_MENU.get(), TagFilterGui::new);
        event.register(FluxPylonsContainerMenus.SMELTER_CONTAINER_MENU.get(), SmelterGui::new);
        event.register(FluxPylonsContainerMenus.WASHER_CONTAINER_MENU.get(), WasherGui::new);
        event.register(FluxPylonsContainerMenus.BOILER_CONTAINER_MENU.get(), BoilerGui::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FluxPylonsBlockEntities.PYLON.get(), PylonBlockEntityRenderer::new);
    }
}
