package com.joelcrosby.fluxpylons.pylon;

import com.joelcrosby.fluxpylons.rendering.DelayedRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

import static com.joelcrosby.fluxpylons.pylon.network.PylonNetworkManager.CONNECTION_RANGE;

public class PylonBlockEntityRenderer implements BlockEntityRenderer<PylonBlockEntity> {

    @SuppressWarnings("unused")
    public PylonBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }
    
    @Override
    public void render(PylonBlockEntity tile, float partialTicks, PoseStack matrixStack, MultiBufferSource source, int light, int overlay) {
        DelayedRenderer.add(tile);
    }

    @Nonnull
    @Override
    public AABB getRenderBoundingBox(PylonBlockEntity blockEntity) {
        return new AABB(
                blockEntity.getBlockPos().above(CONNECTION_RANGE).north(CONNECTION_RANGE).east(CONNECTION_RANGE)
                // TODO: is this needed
//                blockEntity.getBlockPos().below(CONNECTION_RANGE).south(CONNECTION_RANGE).west(CONNECTION_RANGE)
        );
    }
}
