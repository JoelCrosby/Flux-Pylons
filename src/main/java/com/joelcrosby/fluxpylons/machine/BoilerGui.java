package com.joelcrosby.fluxpylons.machine;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.machine.common.MachineGui;
import com.joelcrosby.fluxpylons.rendering.TankRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

public class BoilerGui extends MachineGui<BoilerContainerMenu, BoilerBlockEntity> {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/boiler.png");

    public BoilerGui(BoilerContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.imageWidth = 176;
        this.imageHeight = 166;

        this.tile = container.tile;
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
        gui.drawString(this.font, this.title.getString(), 46, 6, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        var i = this.leftPos;
        var j = this.topPos;

        int progress = getTemperature();

        gui.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        gui.blit(TEXTURE, i + 44, j + 65, 176, 14, progress, 2);

        tile.getFluidStack().ifPresent(fluidStack -> {
            TankRenderer.renderGuiTank(fluidStack, tile.getFluidTankCapacity(), i + 8, j + 8, 0, 16, 59);
        });

        tile.getFluidStack(1).ifPresent(fluidStack -> {
            TankRenderer.renderGuiTank(fluidStack, tile.getFluidTankCapacity(1), i + 151, j + 8, 0, 16, 59);
        });

    }

    @Override
    protected void renderTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        tile.getFluidStack().ifPresent(fluidStack -> {
            if (isHovering(8, 8, 16, 67, mouseX, mouseY)) {
                gui.renderTooltip(this.font, Utility.tankTooltip(fluidStack, tile.getFluidTankCapacity()), Optional.empty(), mouseX, mouseY);
            }
        });

        tile.getFluidStack(1).ifPresent(fluidStack -> {
            if (isHovering(151, 8, 16, 67, mouseX, mouseY)) {
                gui.renderTooltip(this.font, Utility.tankTooltip(fluidStack, tile.getFluidTankCapacity(1)), Optional.empty(), mouseX, mouseY);
            }
        });

        if (isHovering(43, 64, 90, 4, mouseX, mouseY)) {
            var temperature = getTemperature();
            var formatter = new DecimalFormat("#,###");
            var stringTemperature = formatter.format(temperature);

            List<Component> components = List.of(
                    Component.translatable("info." + FluxPylons.ID + ".temperature").append(": "),
                    Component.literal(stringTemperature).append(" ").append(Component.translatable("info." + FluxPylons.ID + ".celsius"))
            );

            gui.renderTooltip(this.font, components, Optional.empty(), mouseX, mouseY);
        }

        super.renderTooltip(gui, mouseX, mouseY);
    }

    private int getTemperature() {
        var temperatureGaugeWidth = 88;
        var progress = this.tile.getTemperature();
        if (progress == 100) return temperatureGaugeWidth;
        return temperatureGaugeWidth - (temperatureGaugeWidth * (100 - progress) / 100);
    }
}
