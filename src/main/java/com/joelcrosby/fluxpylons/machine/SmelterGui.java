package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.machine.common.MachineGui;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SmelterGui extends MachineGui<SmelterContainerMenu, SmelterBlockEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/smelter.png");

    public SmelterGui(SmelterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.tile = container.tile;
    }
    
    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        var i = this.leftPos;
        var j = this.topPos;
        
        int power = getEnergyBar(44);
        int progress = getProgressBar(24);

        gui.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        gui.blit(TEXTURE, i + 89, j + 34, 176, 0, progress, 17);
        gui.blit(TEXTURE, i + 9, j + (7 + (44 - power)), 176, 17 + (44 - power), 14, power);
    }
}
