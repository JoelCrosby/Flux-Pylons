package com.joelcrosby.fluxpylons.item.upgrade.filter;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.gui.ToggleButton;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.BaseFilterItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.common.FilterSlotHandler;
import com.joelcrosby.fluxpylons.network.packets.PacketGhostSlot;
import com.joelcrosby.fluxpylons.network.packets.PacketUpdateTagFilter;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TagFilterGui extends AbstractContainerScreen<TagFilterContainerMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/tag_filter.png");

    protected final TagFilterContainerMenu container;
    protected final ItemStack filterItem;
    protected final BaseFilterItem item;

    private int scrollOffset;
    private boolean isScrolling;

    private boolean isDenyList;

    private List<String> tags;

    private final List<String> selectedTags;
    private final List<TagListItem> tagListItems = new ArrayList<>();

    private static final int LIST_ITEM_SCROLL_SIZE = 10;
    private static final int LIST_WIDTH = 124;

    public TagFilterGui(TagFilterContainerMenu container, Inventory inv, Component titleIn) {
        super(container, inv, titleIn);

        this.imageWidth = 176;
        this.imageHeight = 222;
        this.container = container;
        this.filterItem = container.filterItem;
        this.item = (BaseFilterItem) filterItem.getItem();

        this.selectedTags = getSelectedTags();
        this.tags = getItemTags();
    }

    private List<String> getItemTags() {
        var filterInventory = TagFilterItem.getInventory(filterItem);
        var tagTargetItem = filterInventory.getStackInSlot(0);

        var slotTags = tagTargetItem.getTags()
                .map(t -> t.location().toString())
                .filter(t -> selectedTags.stream().noneMatch(t::equals))
                .sorted();

        return Stream.concat(selectedTags.stream().sorted(), slotTags).collect(Collectors.toList());
    }

    private List<String> getSelectedTags() {
        var selectedTags = TagFilterItem.getTags(filterItem);
        return selectedTags.stream().sorted().collect(Collectors.toList());
    }

    @Override
    protected void init() {
        super.init();

        var allowDenyTextures = new ResourceLocation[] {
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_allow.png"),
                ResourceLocation.fromNamespaceAndPath(FluxPylons.ID, "textures/gui/buttons/btn_deny.png"),
        };

        var allowDenyTooltips = new String[] {
                "item.fluxpylons.filter.tooltip.allow",
                "item.fluxpylons.filter.tooltip.deny",
        };

        isDenyList = BaseFilterItem.getIsDenyList(filterItem);

        var allowDenyX = getGuiLeft() + 8;
        var allowDenyY = getGuiTop() + 54;

        var allowDenyBtn = new ToggleButton(this, allowDenyX, allowDenyY, allowDenyTextures, allowDenyTooltips, isDenyList ? 1 : 0, (btn) -> {
            isDenyList = !isDenyList;
            ((ToggleButton) btn).setTexturePosition(isDenyList ? 1 : 0);
        });

        addRenderableWidget(allowDenyBtn);

        this.updateWidgets();
    }

    @Override
    protected void renderBg(GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        gui.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.updateWidgets();

        for (var tag : this.tagListItems)
            tag.draw(gui, mouseX, mouseY);

        if (this.tags.size() >= LIST_ITEM_SCROLL_SIZE) {
            var percentage = this.scrollOffset / (float) (this.tags.size() - (LIST_ITEM_SCROLL_SIZE - 1));
            gui.blit(TEXTURE, this.leftPos + 156, this.topPos + 32 + 16 + (int) (percentage * (58 - 15)), 232, 241, 12, 15);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var tag : this.tagListItems) {
            if (tag.onClicked(mouseX, mouseY, button))
                return true;
        }

        if (button == 0 && mouseX >= this.leftPos + 156 && this.topPos + mouseY >= 32 + 16 && mouseX < this.leftPos + 156 + 12 && mouseY < this.topPos + 32 + 16 + 58) {
            this.isScrolling = true;
            return true;
        }

        if (hoveredSlot == null || !(hoveredSlot instanceof FilterSlotHandler)) {
            return super.mouseClicked(mouseX, mouseY, button);
        }

        var stack = this.menu.getCarried();
        stack = stack.copy().split(hoveredSlot.getMaxStackSize());

        if (ItemStack.isSameItemSameComponents(stack, container.filterItem)) {
            return true;
        }

        hoveredSlot.set(stack);

        PacketDistributor.sendToServer(new PacketGhostSlot(hoveredSlot.index, stack, stack.getCount()));

        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            this.isScrolling = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int i, double j, double k) {
        if (this.isScrolling) {
            var percentage = Mth.clamp(((float) mouseY - (this.topPos + 32 + 18)) / (58 - 15), 0, 1);
            var offset = (int) (percentage * (float) (this.tags.size() - (LIST_ITEM_SCROLL_SIZE - 1)));
            if (offset != this.scrollOffset) {
                this.scrollOffset = offset;
                this.updateWidgets();
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, i, j, k);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (this.tags.size() >= LIST_ITEM_SCROLL_SIZE) {
            var offset = Mth.clamp(this.scrollOffset - (int) Math.signum(deltaY), 0, this.tags.size() - (LIST_ITEM_SCROLL_SIZE - 1));
            if (offset != this.scrollOffset) {
                this.scrollOffset = offset;
                this.updateWidgets();
            }
        }
        return true;
    }

    private void updateWidgets() {
        this.tags = getItemTags();
        this.tagListItems.clear();
        for (var i = 0; i < (LIST_ITEM_SCROLL_SIZE - 1); i++) {
            if (i >= this.tags.size())
                break;
            this.tagListItems.add(new TagListItem(this.tags.get(this.scrollOffset + i), this.leftPos + 46, this.topPos + 19 + i * 12));
        }
    }

    private void updateSelectedTags() {
        PacketDistributor.sendToServer(new PacketUpdateTagFilter(isDenyList, selectedTags));
    }

    @Override
    protected void renderLabels(GuiGraphics gui, int mouseX, int mouseY) {
        gui.drawString(this.font, this.playerInventoryTitle.getString(), 8, this.imageHeight - 96 + 2, 4210752, false);
        gui.drawString(this.font, this.title.getString(), 8, 6, 4210752, false);

        renderTooltip(gui, mouseX - leftPos, mouseY - topPos);
    }

    @Override
    public void onClose() {
        updateSelectedTags();
        super.onClose();
    }

    private class TagListItem {

        private final String tag;
        private final int x;
        private final int y;

        public TagListItem(String tag, int x, int y) {
            this.tag = tag;
            this.x = x;
            this.y = y;
        }

        private void draw(GuiGraphics gui, double mouseX, double mouseY) {
            var color = 4210752;

            var isSelected = selectedTags.stream().anyMatch(t -> Objects.equals(t, tag));

            if (isSelected) {
                color = 0x1B7491;
            }

            if (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + LIST_WIDTH && mouseY < this.y + 12) {
                color = 0xFFFFFF;

                if (isSelected) {
                    color = 0x42a8c9;
                }

                var pose = gui.pose();
                pose.pushPose();
                RenderSystem.disableDepthTest();
                RenderSystem.colorMask(true, true, true, false);

                gui.fillGradient(this.x - 2, this.y - 1, this.x + (LIST_WIDTH - 2), this.y + LIST_ITEM_SCROLL_SIZE, 0x885B5B5B, 0x885B5B5B);

                if (Utility.inBounds(this.x - 2, this.y - 1, LIST_ITEM_SCROLL_SIZE, LIST_ITEM_SCROLL_SIZE, mouseX, mouseY)) {
                    var tooltip = Component.translatable(this.tag);
                    gui.renderTooltip(TagFilterGui.this.font, tooltip, this.x, this.y);
                }

                RenderSystem.colorMask(true, true, true, true);

                pose.popPose();
            }

            gui.drawString(TagFilterGui.this.font, this.tag, this.x, this.y + 1, color);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
        }

        private boolean onClicked(double mouseX, double mouseY, int button) {
            if (button != 0)
                return false;
            if (mouseX < this.x || mouseY < this.y || mouseX >= this.x + 140 || mouseY >= this.y + 12)
                return false;

            if (selectedTags.contains(this.tag)) {
                selectedTags.remove(this.tag);
            } else {
                selectedTags.add(this.tag);
            }

            updateSelectedTags();

            TagFilterGui.this.getMinecraft().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1));
            return true;
        }
    }
}
