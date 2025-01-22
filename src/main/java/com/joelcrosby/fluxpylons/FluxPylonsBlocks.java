package com.joelcrosby.fluxpylons;

import com.joelcrosby.fluxpylons.crate.CrateBlock;
import com.joelcrosby.fluxpylons.machine.SmelterBlock;
import com.joelcrosby.fluxpylons.machine.WasherBlock;
import com.joelcrosby.fluxpylons.pipe.PipeBlock;
import com.joelcrosby.fluxpylons.pipe.PipeType;
import com.joelcrosby.fluxpylons.pylon.PylonBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FluxPylonsBlocks
{
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(FluxPylons.ID);


    public static final Item.Properties ITEM_PROPERTIES = new Item.Properties();

    public static DeferredHolder<Block, PipeBlock> BASIC_PIPE = BLOCKS.register("pipe", () -> new PipeBlock(BlockBehaviour.Properties.of(), PipeType.BASIC));
    public static DeferredHolder<Item, BlockItem> BASIC_PIPE_ITEM = fromBlock(BASIC_PIPE);

    public static DeferredHolder<Block, PipeBlock> ADV_PIPE = BLOCKS.register("adv_pipe", () -> new PipeBlock(BlockBehaviour.Properties.of(), PipeType.ADVANCED));
    public static DeferredHolder<Item, BlockItem> ADV_PIPE_ITEM = fromBlock(ADV_PIPE);

    public static DeferredHolder<Block, CrateBlock> CRATE = BLOCKS.register("crate", CrateBlock::new);
    public static DeferredHolder<Item, BlockItem> CRATE_ITEM = fromBlock(CRATE);

    public static DeferredHolder<Block, PylonBlock> PYLON = BLOCKS.register("pylon", PylonBlock::new);
    public static DeferredHolder<Item, BlockItem> PYLON_ITEM = fromBlock(PYLON);

    public static DeferredHolder<Block, SmelterBlock> SMELTER = BLOCKS.register("smelter", SmelterBlock::new);
    public static DeferredHolder<Item, BlockItem> SMELTER_ITEM = fromBlock(SMELTER);

    public static DeferredHolder<Block, WasherBlock> WASHER = BLOCKS.register("washer", WasherBlock::new);
    public static DeferredHolder<Item, BlockItem> WASHER_ITEM = fromBlock(WASHER);

    public static <B extends Block> DeferredHolder<Item, BlockItem> fromBlock(DeferredHolder<Block, B> block) {
        return FluxPylonsItems.ITEM_REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), ITEM_PROPERTIES));
    }
}
