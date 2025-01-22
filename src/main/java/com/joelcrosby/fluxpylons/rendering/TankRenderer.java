package com.joelcrosby.fluxpylons.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

public class TankRenderer {
    public static void renderGuiTank(IFluidHandler fluidHandler, int tank, double x, double y, double zLevel, double width, double height) {
        var stack = fluidHandler.getFluidInTank(tank);
        var tankCapacity = fluidHandler.getTankCapacity(tank);

        renderGuiTank(stack, tankCapacity, x, y, zLevel, width, height);
    }

    public static void renderGuiTank(FluidStack stack, int tankCapacity, double x, double y, double zLevel, double width, double height) {
        // Originally Adapted from Ender IO by Silent's Mechanisms
        int amount;
        try {
            if (stack.isEmpty()) {
                return;
            }
        } catch (Exception e) {
            return;
        }

        try {
            amount = stack.getAmount();
        } catch (Exception e) {
            amount = 0;
        }

        var sprite = getFluidTexture(stack);
        if (sprite == null) {
            return;
        }

        var renderAmount = (int) Math.max(Math.min(height, amount * height / tankCapacity), 1);
        var posY = (int) (y + height - renderAmount);

        RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);

        var color = IClientFluidTypeExtensions.of(stack.getFluid()).getTintColor();

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);
        RenderSystem.enableBlend();

        for (int i = 0; i < width; i += 16) {
            for (int j = 0; j < renderAmount; j += 16) {
                var drawWidth = (int) Math.min(width - i, 16);
                var drawHeight = Math.min(renderAmount - j, 16);

                var drawX = (int) (x + i);
                var drawY = posY + j;

                var minU = sprite.getU0(); // min
                var maxU = sprite.getU1(); // max
                var minV = sprite.getV0(); // min
                var maxV = sprite.getV1(); // max

                var buffer = Tesselator.getInstance()
                        .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
                buffer.addVertex(drawX, drawY + drawHeight, 0)
                        .setUv(minU, minV + (maxV - minV) * drawHeight / 16F);
                buffer.addVertex(drawX + drawWidth, drawY + drawHeight, 0)
                        .setUv(minU + (maxU - minU) * drawWidth / 16F, minV + (maxV - minV) * drawHeight / 16F);
                buffer.addVertex(drawX + drawWidth, drawY, 0)
                        .setUv(minU + (maxU - minU) * drawWidth / 16F, minV);
                buffer.addVertex(drawX, drawY, 0)
                        .setUv(minU, minV);

                var mesh = buffer.build();
                if (mesh != null) {
                    BufferUploader.drawWithShader(mesh);
                }

            }
        }

        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f,  1f);
    }

    @Nullable
    public static TextureAtlasSprite getFluidTexture(FluidStack stack) {
        var sprites = FluidSpriteCache.getFluidSprites(Minecraft.getInstance().level, BlockPos.ZERO, stack.getFluid().defaultFluidState());
        return sprites.length > 0 ? sprites[0] : null;
    }
}
