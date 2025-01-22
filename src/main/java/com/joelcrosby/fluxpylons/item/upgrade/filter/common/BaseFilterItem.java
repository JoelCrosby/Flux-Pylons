package com.joelcrosby.fluxpylons.item.upgrade.filter.common;

import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsDataComponents;
import com.joelcrosby.fluxpylons.data.InteractionSide;
import com.joelcrosby.fluxpylons.data.TagList;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.item.upgrade.filter.TagFilterItem;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class BaseFilterItem extends UpgradeItem {

    protected abstract int getSlots();

    public abstract void openGui(Player player, ItemStack stack);

    @Override
    public void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType) {

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    public static void setIsDenyList(ItemStack stack, boolean isDenyList) {
        stack.set(FluxPylonsDataComponents.IS_DENY_LIST, isDenyList);
    }

    public static void setTags(ItemStack stack, List<String> tags) {
        stack.set(FluxPylonsDataComponents.TAGS, new TagList(tags));
    }

    public static List<String> getTags(ItemStack stack) {
        return stack.getOrDefault(FluxPylonsDataComponents.TAGS, TagList.Empty).tags();
    }

    public static boolean getIsDenyList(ItemStack stack) {
        return stack.getOrDefault(FluxPylonsDataComponents.IS_DENY_LIST, true);
    }

    public static void setMatchNbt(ItemStack stack, boolean matchNbt) {
        stack.set(FluxPylonsDataComponents.MATCH_NBT, matchNbt);
    }

    public static boolean getMatchNbt(ItemStack stack) {
        return stack.getOrDefault(FluxPylonsDataComponents.MATCH_NBT, false);
    }

    public static void setInteractionSide(ItemStack stack, @Nullable Direction direction) {
        stack.set(FluxPylonsDataComponents.INTERACTION_SIDE, new InteractionSide(direction));
    }

    @Nullable
    public static Direction getInteractionSide(ItemStack stack) {
        return stack.has(FluxPylonsDataComponents.INTERACTION_SIDE)
            ? Objects.requireNonNull(stack.get(FluxPylonsDataComponents.INTERACTION_SIDE)).direction()
                : null;
    }

    protected boolean supportsNbtMatch() {
        return true;
    }

    protected boolean supportsInteractionSide() {
        return false;
    }

    public static ItemFilterStackHandler getInventory(ItemStack stack) {
        var item = (BaseFilterItem) stack.getItem();
        return new ItemFilterStackHandler(item.getSlots(), stack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        var mc = Minecraft.getInstance();

        if (mc.player == null) return;

        var sneakPressed = Screen.hasShiftDown();

        if (sneakPressed) {
            var inventory = getInventory(stack);
            var isDenyList = getIsDenyList(stack);
            var matchNbt = getMatchNbt(stack);

            var prefix = "item.fluxpylons.filter.tooltip.";

            var isDenyComponent = Component.translatable(prefix + (isDenyList ? "deny" : "allow")).setStyle(Style.EMPTY.applyFormat(isDenyList ? ChatFormatting.RED : ChatFormatting.DARK_GREEN));
            var matchNbtComponent = Component.translatable(prefix + (matchNbt ? "match-nbt" : "ignore-nbt")).setStyle(Style.EMPTY.applyFormat(matchNbt ? ChatFormatting.DARK_GREEN : ChatFormatting.RED));

            var divider = Component.literal(" | ").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GRAY));

            tooltip.add(isDenyComponent.append(supportsNbtMatch() ? divider.append(matchNbtComponent) : Component.empty()));

            if (stack.getItem() instanceof TagFilterItem) {
                var tags = getTags(stack);

                for (var tag : tags) {
                    tooltip.add(Component.literal(""));
                    tooltip.add(Component.translatable(tag).withStyle(ChatFormatting.DARK_AQUA));
                }
            } else {
                for (var i = 0; i < inventory.getSlots(); i++) {
                    var stackInSlot = inventory.getStackInSlot(i);

                    if (stackInSlot.isEmpty()) continue;

                    if (i == 0) {
                        tooltip.add(Component.literal(""));
                    }

                    tooltip.add(Component.translatable(stackInSlot.getItem().getDescriptionId()).withStyle(ChatFormatting.GOLD));
                }
            }

        } else {
            tooltip.add(Component.translatable("info." + FluxPylons.ID + ".hold").withStyle(ChatFormatting.GRAY)
                   .append(Component.literal(" "))
                   .append(Component.translatable("info." + FluxPylons.ID + ".shift").withStyle(ChatFormatting.AQUA)
                   .append(Component.literal(" "))
                   .append(Component.translatable("info." + FluxPylons.ID + ".more_info").withStyle(ChatFormatting.GRAY)
           )));
        }
    }
}
