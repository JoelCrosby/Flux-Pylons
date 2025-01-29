package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.machine.common.MachineGui;
import com.joelcrosby.fluxpylons.rendering.TankRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.Optional;

public class WasherGui extends MachineGui<WasherContainerMenu, WasherBlockEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/washer.png");

    public WasherGui(WasherContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.tile = container.tile;
    }

    public static Rect2i getTankRect() {
        return new Rect2i(42, 19, 17, 47);
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

        tile.getFluidStack().ifPresent(fluidStack -> {
            TankRenderer.renderGuiTank(fluidStack, tile.getFluidTankCapacity(), i + 42, j + 19, 0, 16, 47);
        });
    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        if (isHovering(42, 19, 16, 47, mouseX, mouseY)) {
            tile.getFluidStack().ifPresent(fluidStack -> {
                gui.renderTooltip(this.font, Utility.tankTooltip(fluidStack, tile.getFluidTankCapacity()), Optional.empty(), mouseX, mouseY);
            });
        }

        super.renderTooltip(gui, mouseX, mouseY);
    }
}
