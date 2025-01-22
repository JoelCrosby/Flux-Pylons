package com.joelcrosby.fluxpylons.machine.common;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;
import java.util.List;

public abstract class MachineGui<T extends MachineContainerMenu<E>, E extends MachineBlockEntity> extends AbstractContainerScreen<T>  {

    protected E tile;

    public MachineGui(T container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.tile = container.tile;
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks){
        this.renderBackground(gui, mouseX, mouseY, partialTicks);
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
        gui.drawString(this.font, this.title.getString(), 66, 6, 4210752, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        if (isHovering(9, 7, 16, 42, mouseX, mouseY)) {
            var storage = tile.getEnergy();
            var formatter = new DecimalFormat("#,###");
            var stored = formatter.format(storage.getEnergyStored());
            var max = formatter.format(storage.getMaxEnergyStored());

            gui.renderTooltip(this.font, Component.nullToEmpty(stored + " FE / " + max + " FE"), mouseX, mouseY);
        }

        super.renderTooltip(gui, mouseX, mouseY);
    }

    public List<Component> getTooltips() {
        var progress = this.tile.getProgress();

        if (progress == 0) {
            return List.of();
        }

        return List.of(Component.nullToEmpty(this.tile.getProgress() + " %"));
    }

    public int getProgressBar(int widthPx) {
        var progress = this.tile.getProgress();
        if (progress == 100) return widthPx;
        return widthPx - (widthPx * (100 - progress) / 100);
    }

    public int getEnergyBar(int heightPx) {
        int stored = tile.getEnergy().getEnergyStored();
        int max = tile.getEnergy().getMaxEnergyStored();

        if (max == 0) return 0;

        return (((stored * 100 / max * 100) / 100) * heightPx) / 100;
    }
}
