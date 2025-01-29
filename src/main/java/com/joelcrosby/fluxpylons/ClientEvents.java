package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.rendering.DelayedRenderer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public class ClientEvents {

    @SubscribeEvent
    public static void renderLevelStageEvent(RenderLevelStageEvent event) {
        DelayedRenderer.render(event.getPoseStack());
    }
}
