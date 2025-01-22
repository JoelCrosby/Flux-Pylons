package com.joelcrosby.fluxpylons.pipe;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.gui.BasicButton;
import com.joelcrosby.fluxpylons.gui.ToggleButton;
import com.joelcrosby.fluxpylons.network.packets.PacketOpenScreen;
import com.joelcrosby.fluxpylons.network.packets.PacketUpdatePipeManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;


public class PipeUpgradeGui extends AbstractContainerScreen<PipeUpgradeContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/pipe.png");
    private final PipeUpgradeContainerMenu container;

    private PipeIoMode ioMode;
    
    public PipeUpgradeGui(PipeUpgradeContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        this.container = container;

        this.imageWidth = 176;
        this.imageHeight = 153;
    }

    @Override
    protected void init() {
        super.init();

        var ioModeTextures = new ResourceLocation[] {
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_insert_extract.png"),
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_insert.png"),
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_extract.png"),
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_side_off.png"),
        };

        var ioModeTooltips = new String[] {
                "item.fluxpylons.filter.tooltip.io_mode_insert_extract",
                "item.fluxpylons.filter.tooltip.io_mode_insert",
                "item.fluxpylons.filter.tooltip.io_mode_extract",
                "item.fluxpylons.filter.tooltip.io_mode_disabled",
        };

        ioMode = container.pipeIoMode;

        var ioModeX = getGuiLeft() + 8;
        var ioModeY = getGuiTop() + 18;

        var ioModeBtn = new ToggleButton(this, ioModeX, ioModeY, ioModeTextures, ioModeTooltips, ioMode.ordinal(), (btn) -> {
            if (ioMode.ordinal() == PipeIoMode.values().length - 1) {
                ioMode = PipeIoMode.values()[0];
            } else {
                ioMode = PipeIoMode.values()[ioMode.ordinal() + 1];
            }
            ((ToggleButton) btn).setTexturePosition(ioMode.ordinal());
        });
        
        addRenderableWidget(ioModeBtn);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.render(gui, mouseX, mouseY, partialTicks);

        this.renderables.stream()
                .filter(widget -> widget instanceof BasicButton)
                .forEach(widget -> ((BasicButton) widget).onRenderToolTip(gui, mouseX, mouseY));
    }
    
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
        gui.drawString(this.font, this.title.getString(), 8, 6, 4210752, false);

        renderTooltip(gui, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (btn == 1 && hoveredSlot != null) {
            var slot = hoveredSlot.getSlotIndex();
            PacketDistributor.sendToServer(new PacketOpenScreen(slot));
            return true;
        }
        
        return super.mouseClicked(x, y, btn);
    }

    // TODO: Fix tooltips

//    @Override
//    protected void renderTooltip(GuiGraphics gui, ItemStack itemStack, int mouseX, int mouseY) {
//        if (mouseY > this.imageHeight - 96 + 2 || mouseX > this.imageWidth - 8 - 36) {
//            super.renderTooltip(gui, itemStack, mouseX, mouseY);
//            return;
//        }
//
//        var components = itemStack.getTooltipLines(minecraft.player, TooltipFlag.Default.NORMAL);
//        components.add(Component.translatable(""));
//        components.add(Component.translatable("item.fluxpylons.filter.tooltip.open-menu").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GRAY)));
//
//        renderTooltip(gui, components, Optional.empty(), mouseX, mouseY);
//    }

    @Override
    public void onClose() {
        PacketDistributor.sendToServer(new PacketUpdatePipeManager(ioMode));
        super.onClose();
    }
}
