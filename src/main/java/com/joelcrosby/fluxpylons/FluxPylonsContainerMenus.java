package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FluidFilterContainerMenu;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.ItemFilterContainerMenu;
import com.joelcrosby.fluxpylons.machine.BoilerContainerMenu;
import com.joelcrosby.fluxpylons.machine.SmelterContainerMenu;
import com.joelcrosby.fluxpylons.machine.WasherContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeContainerMenu;
import com.joelcrosby.fluxpylons.pipe.PipeUpgradeItemStackHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

public class FluxPylonsContainerMenus {
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, FluxPylons.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CrateContainerMenu>> CRATE_CONTAINER_MENU = CONTAINERS.register("crate", () -> IMenuTypeExtension.create((id, inv, data) -> new CrateContainerMenu(id, inv.player, data.readBlockPos())));
    public static final DeferredHolder<MenuType<?>, MenuType<PipeUpgradeContainerMenu>> PIPE_UPGRADE_CONTAINER_MENU = CONTAINERS.register("upgrade", () -> IMenuTypeExtension.create((id, inv, data) -> new PipeUpgradeContainerMenu(id, inv.player, new PipeUpgradeItemStackHandler(), data)));
    public static final DeferredHolder<MenuType<?>, MenuType<ItemFilterContainerMenu>> UPGRADE_FILTER_CONTAINER_MENU = CONTAINERS.register("filter", () -> IMenuTypeExtension.create((id, inv, data) -> new ItemFilterContainerMenu(id, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<FluidFilterContainerMenu>> UPGRADE_FLUID_FILTER_CONTAINER_MENU = CONTAINERS.register("fluid_filter", () -> IMenuTypeExtension.create((id, inv, data) -> new FluidFilterContainerMenu(id, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<TagFilterContainerMenu>> UPGRADE_TAG_FILTER_CONTAINER_MENU = CONTAINERS.register("tag_filter", () -> IMenuTypeExtension.create((id, inv, data) -> new TagFilterContainerMenu(id, inv, inv.player, data)));
    public static final DeferredHolder<MenuType<?>, MenuType<SmelterContainerMenu>> SMELTER_CONTAINER_MENU = CONTAINERS.register("smelter", () -> IMenuTypeExtension.create((id, inv, data) -> new SmelterContainerMenu(id, inv.player, data.readBlockPos())));
    public static final DeferredHolder<MenuType<?>, MenuType<WasherContainerMenu>> WASHER_CONTAINER_MENU = CONTAINERS.register("washer", () -> IMenuTypeExtension.create((id, inv, data) -> new WasherContainerMenu(id, inv.player, data.readBlockPos())));
    public static final DeferredHolder<MenuType<?>, MenuType<BoilerContainerMenu>> BOILER_CONTAINER_MENU = CONTAINERS.register("boiler", () -> IMenuTypeExtension.create((id, inv, data) -> new BoilerContainerMenu(id, inv.player, data.readBlockPos())));

    public static final int BaseFilterContainerSlots = 14;
}
