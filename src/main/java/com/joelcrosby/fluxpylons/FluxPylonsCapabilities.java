package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.pipe.IPipeConnectable;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class FluxPylonsCapabilities {

    public static final BlockCapability<IPipeConnectable, Direction> PipeConnectableCapability = BlockCapability.createSided(ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "pipe_connectable"), IPipeConnectable.class);

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(FluxPylonsCapabilities.PipeConnectableCapability, FluxPylonsBlockEntities.BASIC_PIPE.get(), (e, d) -> e);
        event.registerBlockEntity(FluxPylonsCapabilities.PipeConnectableCapability, FluxPylonsBlockEntities.ADV_PIPE.get(), (e, d) -> e);

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FluxPylonsBlockEntities.BASIC_PIPE.get(), (e, d) -> e.getUpgradeManager(d).pipeUpgradeContainer.getItems());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FluxPylonsBlockEntities.ADV_PIPE.get(), (e, d) -> e.getUpgradeManager(d).pipeUpgradeContainer.getItems());

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FluxPylonsBlockEntities.BASIC_PIPE.get(), PipeBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FluxPylonsBlockEntities.ADV_PIPE.get(), PipeBlockEntity::getEnergyStorage);
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FluxPylonsBlockEntities.PYLON.get(), PylonBlockEntity::getEnergyStorage);

        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FluxPylonsBlockEntities.WASHER.get(), (e, d) -> e.getEnergy());
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, FluxPylonsBlockEntities.SMELTER.get(), (e, d) -> e.getEnergy());

        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FluxPylonsBlockEntities.CRATE.get(), (e, d) -> e.items);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FluxPylonsBlockEntities.WASHER.get(), (e, d) -> e.getCapabilityHandler().getItemHandlerCapability());
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, FluxPylonsBlockEntities.SMELTER.get(), (e, d) -> e.getCapabilityHandler().getItemHandlerCapability());

        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, FluxPylonsBlockEntities.WASHER.get(), (e, d) -> e.getCapabilityHandler().fluids());

    }
}
