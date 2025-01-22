package com.joelcrosby.fluxpylons.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.model.data.ModelData;


public class PipeRenderer implements BlockEntityRenderer<PipeBlockEntity> {

    public PipeRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(PipeBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource source, int light, int overlay) {
        if (tile.cover == null) {
            return;
        }

        matrixStack.pushPose();

        var renderer = Minecraft.getInstance().getBlockRenderer();
        var model = renderer.getBlockModel(tile.cover);

        for (var layer : model.getRenderTypes(tile.cover, RandomSource.create(tile.cover.getSeed(tile.getBlockPos())), ModelData.EMPTY)) {
            renderer
                .getModelRenderer()
                .tesselateBlock(
                            tile.getLevel(),
                            model,
                            tile.cover,
                            tile.getBlockPos(),
                            matrixStack,
                            source.getBuffer(layer),
                            true,
                            RandomSource.create(),
                            tile.cover.getSeed(tile.getBlockPos()),
                            overlay,
                            ModelData.EMPTY,
                            layer);
        }

        matrixStack.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(PipeBlockEntity blockEntity) {
        // our render bounding box should always be the full block in case we're covered
        return new AABB(blockEntity.getBlockPos());
    }
}
