package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseInputSlot;
import com.joelcrosby.fluxpylons.machine.common.MachineContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.BOILER;

public class BoilerContainerMenu extends MachineContainerMenu<BoilerBlockEntity> {

    public BoilerContainerMenu(int id, Player player, BlockPos pos) {
        super(BoilerBlockEntity.class, FluxPylonsContainerMenus.BOILER_CONTAINER_MENU.get(), id, player, pos);
    }

    @Override
    public void addOwnSlots() {
        var handler = tile.getCapabilityHandler().items();
        var validItems = getValidItems();

        if (handler != null) {
            addSlot(new BaseInputSlot(handler, validItems, 0, 62, 41));
            addSlot(new BaseInputSlot(handler, validItems, 1, 80, 41));
            addSlot(new BaseInputSlot(handler, validItems, 2, 98, 41));
        }
    }

    private List<ItemStack> getValidItems() {
        var recipes = Objects.requireNonNull(Minecraft.getInstance().level)
                .getRecipeManager()
                .getAllRecipesFor(BOILER.get());

        return recipes.stream().flatMap(r -> Arrays.stream(r.value().fuel.getItems())).toList();
    }
}
