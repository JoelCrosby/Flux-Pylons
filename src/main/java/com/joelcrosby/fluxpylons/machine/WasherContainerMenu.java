package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylonsContainerMenus;
import com.joelcrosby.fluxpylons.container.BaseEnergySlot;
import com.joelcrosby.fluxpylons.container.BaseInputSlot;
import com.joelcrosby.fluxpylons.container.BaseOutputSlot;
import com.joelcrosby.fluxpylons.machine.common.MachineContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

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
        var handler = tile.getCapabilityHandler().itemHandler();

        if (handler.isPresent()) {
            addSlot(new BaseInputSlot(handler.get(), 0, 66, 35));

            addSlot(new BaseOutputSlot(handler.get(), 1, 128, 35));
            addSlot(new BaseOutputSlot(handler.get(), 2, 148, 35));

            addSlot(new BaseEnergySlot(handler.get(), 3, 8, 53));
        }
    }
}
