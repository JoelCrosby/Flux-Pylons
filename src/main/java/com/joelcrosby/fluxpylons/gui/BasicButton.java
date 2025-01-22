package com.joelcrosby.fluxpylons.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class BasicButton extends Button {
    private static final int sizeX = 16;
    private static final int sizeY = 16;

    
    public BasicButton(int x, int y, OnPress onPress) {
        super(x, y, sizeX, sizeY, Component.empty(), onPress, Button.DEFAULT_NARRATION);
    }

    public void renderWidget(GuiGraphics gui, int pMouseX, int pMouseY, float pPartialTick) {
        if (this.visible) {
            this.isHovered = pMouseX >= this.getX() && pMouseY >= this.getY() && pMouseX < this.getX() + this.width && pMouseY < this.getY() + this.height;
            super.render(gui, pMouseX, pMouseY, pPartialTick);
        }
    }

    public void onRenderToolTip(GuiGraphics gui, int x, int y) {
//        gui.render super.renderToolTip(gui, x, y);
    }

    @Override
    public void onClick(double x, double y) {
        super.onClick(x, y);
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        return super.mouseClicked(x, y, button);
    }
}
