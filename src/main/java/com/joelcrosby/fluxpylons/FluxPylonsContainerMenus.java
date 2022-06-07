package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

public class FluxPylonsContainerMenus {
    @ObjectHolder(FluxPylons.ID + ":crate")
    public static final MenuType<CrateContainerMenu> CRATE_CONTAINER_MENU = null;
    @ObjectHolder(FluxPylons.ID + ":upgrade")
    public static final MenuType<PipeUpgradeContainerMenu> PIPE_UPGRADE_CONTAINER_MENU = null;
    @ObjectHolder(FluxPylons.ID + ":filter")
    public static final MenuType<FilterContainerMenu> UPGRADE_FILTER_CONTAINER_MENU = null;
    @ObjectHolder(FluxPylons.ID + ":fluid_filter")
    public static final MenuType<FluidFilterContainerMenu> UPGRADE_FLUID_FILTER_CONTAINER_MENU = null;
    
}
