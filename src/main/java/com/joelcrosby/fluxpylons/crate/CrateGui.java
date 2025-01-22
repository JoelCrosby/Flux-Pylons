package com.joelcrosby.fluxpylons.crate;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CrateGui extends AbstractContainerScreen<CrateContainerMenu> {

    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/crate.png");

    public CrateGui(CrateContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        
        this.imageWidth = 176;
        this.imageHeight = 222;
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        gui.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
        gui.drawString(this.font, this.title.getString(), 8, 6, 4210752, false);

        renderTooltip(gui, mouseX - leftPos, mouseY - topPos);
    }
}
