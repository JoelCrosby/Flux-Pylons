package com.joelcrosby.fluxpylons.rendering;

import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Queue;

public class PylonRenderer {
    public static void render(Queue<PylonBlockEntity> tiles, PoseStack matrixStack) {
        var buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        var projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        var builder = buffer.getBuffer(RenderTypes.PYLON_BEAM);

        while (tiles.size() > 0) {
            var tile = tiles.remove();
            var startPos = tile.getBlockPos();

            var level = tile.getLevel();
            var gameTime = level.getGameTime();
            
            var v = gameTime * 0.04;
    
            matrixStack.pushPose();
            var positionMatrix = matrixStack.last().pose();
    
            matrixStack.translate(startPos.getX() - projectedView.x,
                    startPos.getY() - projectedView.y,
                    startPos.getZ() - projectedView.z);
    
            var startVec = new Vector3f(.5f, .5f, .5f);
            var thickness = 1f / 16;
            
            var connections = tile.getConnections();
            
            for (var connection : connections) {
                var diffX = connection.getX() + .5f - startPos.getX();
                var diffY = connection.getY() + .5f - startPos.getY();
                var diffZ = connection.getZ() + .5f - startPos.getZ();
                var endVec = new Vector3f(diffX, diffY, diffZ);
                
                drawBeam(builder, positionMatrix, endVec, startVec, 0, 0, 1, 0.72f, thickness, v, v + diffY * 1.5, tile);
            }
            
            matrixStack.popPose();
        }
        
        buffer.endBatch(RenderTypes.PYLON_BEAM);
    }

    public static void drawBeam(VertexConsumer builder, Matrix4f positionMatrix, Vector3f from, Vector3f to, float r, float g, float b, float alpha, float thickness, double v1, double v2, BlockEntity be) {
        var adjustedVec = adjustBeamToEyes(from, to, be);
        adjustedVec.mul(thickness); // Determines how thick the beam is

        var startA = from.copy();
        startA.add(adjustedVec);

        var startB = from.copy();
        startB.sub(adjustedVec);

        var endA = to.copy();
        endA.add(adjustedVec);

        var endB = to.copy();
        endB.sub(adjustedVec);
            
        renderQuad(builder, positionMatrix, r, g, b, alpha, v1, v2, startA, startB, endA, endB);
    }

    private static void renderQuad(VertexConsumer builder, Matrix4f positionMatrix, float r, float g, float b, float alpha, double v1, double v2, Vector3f startA, Vector3f startB, Vector3f endA, Vector3f endB) {
        addVertex(positionMatrix, builder, startA, r, g, b, alpha, v1, 1);
        addVertex(positionMatrix, builder, endA,   r, g, b, alpha, v2, 1);
        addVertex(positionMatrix, builder, endB,   r, g, b, alpha, v2, 0);
        addVertex(positionMatrix, builder, startB, r, g, b, alpha, v1, 0);
    }

    private static void addVertex(Matrix4f positionMatrix, VertexConsumer builder, Vector3f vec, float r, float g, float b, float alpha, double v, int uv) {
        builder.vertex(positionMatrix, vec.x(), vec.y(), vec.z())
                .color(r, g, b, alpha)
                .uv(uv, (float) v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880)
                .endVertex();
    }

    public static Vector3f adjustBeamToEyes(Vector3f from, Vector3f to, BlockEntity be) {
        // This method takes the player's position into account, and adjusts the beam so that its rendered properly where ever you stand
        var player = Minecraft.getInstance().player;
        var P = new Vector3f((float) player.getX() - be.getBlockPos().getX(), (float) player.getEyeY() - be.getBlockPos().getY(), (float) player.getZ() - be.getBlockPos().getZ());

        var PS = from.copy();
        PS.sub(P);
        var SE = to.copy();
        SE.sub(from);

        var adjustedVec = PS.copy();
        adjustedVec.cross(SE);
        adjustedVec.normalize();
        
        return adjustedVec;
    }
}
