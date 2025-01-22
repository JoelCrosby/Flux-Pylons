package com.joelcrosby.fluxpylons.item.upgrade;

import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNode;
import com.joelcrosby.fluxpylons.pipe.network.graph.GraphNodeType;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;


public abstract class UpgradeItem extends Item {
    public UpgradeItem() {
        super(new Item.Properties().rarity(Rarity.UNCOMMON));
    }

    public abstract void update(ItemStack itemStack, GraphNode node, Direction dir, GraphNodeType nodeType);

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();
        var pos = context.getClickedPos();
        var player = context.getPlayer();
        var state = level.getBlockState(pos);
        var block = state.getBlock();

        if (!(block instanceof PipeBlock)) {
            return InteractionResult.FAIL;
        }

        if (player != null && !player.isCrouching()) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Utility.addTooltip(BuiltInRegistries.ITEM.getKey(this).getPath(), tooltipComponents);
    }
}
