package com.joelcrosby.fluxpylons.gui;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ToggleButton extends BasicButton {
    protected final Screen screen;
    protected final ResourceLocation[] textures;
    protected final String[] tooltips;
    protected int texturePosition;
    
    private static final ResourceLocation BtnBase = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_base.png");
    private static final ResourceLocation BtnBaseHover = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_base_hover.png");
    
    public ToggleButton(Screen screen, int x, int y, ResourceLocation[] textures, String[] tooltips, int index, OnPress onPress) {
        super(x, y, onPress);
        this.screen = screen;

        this.textures = textures;
        this.tooltips = tooltips;

        setTexturePosition(index);
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.isHovered = mouseX >= this.getX() && mouseY >= this.getY() && mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, isHovered ? BtnBaseHover : BtnBase);

        gui.blit(isHovered ? BtnBaseHover : BtnBase, this.getX(), this.getY(), 0, 0, width, height, width, height);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, textures[texturePosition]);

        gui.blit(textures[texturePosition], this.getX(), this.getY(), 0, 0, width, height, width, height);
    }

    @Override
    public void onRenderToolTip(GuiGraphics gui, int x, int y) {
        if (isHovered) {
            gui.renderTooltip(this.screen.getMinecraft().font, Component.translatable(tooltips[texturePosition]), x, y);
        }
    }
    
    public int getTexturePosition() {
        return texturePosition;
    }

    public void setTexturePosition(int texturePosition) {
        this.texturePosition = Math.min(texturePosition, textures.length);
    }

    public void nextTexturePosition() {
        if (texturePosition == textures.length)
            texturePosition = 0;
        else
            texturePosition++;
    }
}
