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

import static com.joelcrosby.fluxpylons.FluxPylonsRecipes.FluxPylonsRecipeTypes.WASHING;

public class WasherContainerMenu extends MachineContainerMenu<WasherBlockEntity> {

    public WasherContainerMenu(int id, Player player, BlockPos pos) {
        super(WasherBlockEntity.class, FluxPylonsContainerMenus.WASHER_CONTAINER_MENU.get(), id, player, pos);
    }

    @Override
    protected int getSlotCount() {
        return 4;
    }

    @Override
    public void addOwnSlots() {
        var handler = tile.getCapabilityHandler().items();
        var validItems = getValidItems();

        if (handler != null) {
            addSlot(new BaseInputSlot(handler, validItems, 0, 66, 35));

            addSlot(new BaseOutputSlot(handler, 1, 128, 35));
            addSlot(new BaseOutputSlot(handler, 2, 148, 35));

            addSlot(new BaseEnergySlot(handler, 3, 8, 53));
        }
    }

    private List<ItemStack> getValidItems() {
        var recipes = Objects.requireNonNull(Minecraft.getInstance().level)
                .getRecipeManager()
                .getAllRecipesFor(WASHING.get());

        return recipes.stream().flatMap(r ->
                r.value().ingredients.stream().flatMap(i -> Arrays.stream(i.getItems()))
        ).toList();
    }
}
