package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseEnergySlot;
import com.joelcrosby.fluxpylons.container.BaseInputSlot;
import com.joelcrosby.fluxpylons.container.BaseOutputSlot;
import com.joelcrosby.fluxpylons.machine.common.MachineContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.SMELTING;

public class SmelterContainerMenu extends MachineContainerMenu<SmelterBlockEntity> {

    public SmelterContainerMenu(int id, Player player, BlockPos pos) {
        super(SmelterBlockEntity.class, FluxPylonsContainerMenus.SMELTER_CONTAINER_MENU.get(), id, player, pos);
    }

    @Override
    public void addOwnSlots() {
        var handler = tile.getCapabilityHandler().items();
        var validItems = getValidItems();

        if (handler != null) {
            addSlot(new BaseInputSlot(handler, validItems, 0, 30, 25));
            addSlot(new BaseInputSlot(handler, validItems, 1, 48, 25));
            addSlot(new BaseInputSlot(handler, validItems, 2, 66, 25));

            addSlot(new BaseInputSlot(handler, validItems, 3, 30, 43));
            addSlot(new BaseInputSlot(handler, validItems, 4, 48, 43));
            addSlot(new BaseInputSlot(handler, validItems, 5, 66, 43));

            addSlot(new BaseOutputSlot(handler, 6, 128, 35));
            addSlot(new BaseOutputSlot(handler, 7, 148, 35));

            addSlot(new BaseEnergySlot(handler, 8, 8, 53));
        }
    }

    private List<ItemStack> getValidItems() {
        var recipes = Objects.requireNonNull(Minecraft.getInstance().level)
                .getRecipeManager()
                .getAllRecipesFor(SMELTING.get());

        return recipes.stream().flatMap(r ->
                r.value().ingredients.stream().flatMap(i -> Arrays.stream(i.getItems()))
        ).toList();
    }
}
