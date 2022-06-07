package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.gui.ToggleButton;
import com.joelcrosby.fluxpylons.network.PacketHandler;
import com.joelcrosby.fluxpylons.network.packets.PacketGhostSlot;
import com.joelcrosby.fluxpylons.network.packets.PacketUpdateFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

public class FilterGui extends AbstractContainerScreen<FilterContainerMenu> {
    private final FilterContainerMenu container;
    private static final ResourceLocation TEXTURE = new ResourceLocation(FluxPylons.ID, "textures/gui/filter.png");
    private final ItemStack filterItem;

    private boolean isDenyList;
    private boolean matchNbt;
    
    public FilterGui(FilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);
        
        this.imageWidth = 176;
        this.imageHeight = 153;
        this.container = container;
        this.filterItem = container.filterItem;
    }

    @Override
    protected void init() {
        super.init();

        var allowDenyTextures = new ResourceLocation[] {
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_allow.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_deny.png"),
        };

        isDenyList = FilterItem.getIsDenyList(filterItem);
        
        var allowDenyX = getGuiLeft() + 8;
        var allowDenyY = getGuiTop() + 18;
        
        var allowDenyBtn = new ToggleButton(allowDenyX, allowDenyY, allowDenyTextures, isDenyList ? 1 : 0, (btn) -> {
            isDenyList = !isDenyList;            
            ((ToggleButton) btn).setTexturePosition(isDenyList ? 1 : 0);
        });

        var matchNbtTextures = new ResourceLocation[] {
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_match_nbt_off.png"),
            new ResourceLocation(FluxPylons.ID, "textures/gui/buttons/btn_match_nbt_on.png"),
        };

        matchNbt = FilterItem.getMatchNbt(filterItem);

        var matchNbtX = getGuiLeft() + 8;
        var matchNbtY = getGuiTop() + 36;

        var matchNbtBtn = new ToggleButton(matchNbtX, matchNbtY, matchNbtTextures, matchNbt ? 1 : 0, (btn) -> {
            matchNbt = !matchNbt;
            ((ToggleButton) btn).setTexturePosition(matchNbt ? 1 : 0);
        });

        addRenderableWidget(allowDenyBtn);
        addRenderableWidget(matchNbtBtn);
    }

    @Override
    public void onClose() {
        PacketHandler.sendToServer(new PacketUpdateFilter(isDenyList, matchNbt));
        super.onClose();
    }
    
    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        renderBackground(poseStack);
        
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        this.blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
        this.font.draw(poseStack, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752);
        this.font.draw(poseStack, this.title.getString(), 8, 6, 4210752);

        renderTooltip(poseStack, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public boolean mouseClicked(double x, double y, int btn) {
        if (hoveredSlot == null || !(hoveredSlot instanceof FilterSlotHandler)) {
            return super.mouseClicked(x, y, btn);
        }

        var stack = this.menu.getCarried();
        stack = stack.copy().split(hoveredSlot.getMaxStackSize());
        
        if (ItemHandlerHelper.canItemStacksStack(stack, container.filterItem)) {
            return true;
        }
        
        hoveredSlot.set(stack); 
        
        PacketHandler.sendToServer(new PacketGhostSlot(hoveredSlot.index, stack, stack.getCount()));

        return true;
    }
}
