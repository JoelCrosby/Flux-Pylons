package com.joelcrosby.fluxpylons;


import com.joelcrosby.fluxpylons.machine.BoilerBlockEntity;
import net.minecraft.core.registries.Registries;
import com.joelcrosby.fluxpylons.crate.CrateBlockEntity;
import com.joelcrosby.fluxpylons.machine.SmelterBlockEntity;
import com.joelcrosby.fluxpylons.machine.WasherBlockEntity;
import com.joelcrosby.fluxpylons.pipe.PipeBlockEntity;
import com.joelcrosby.fluxpylons.pipe.PipeType;
import com.joelcrosby.fluxpylons.pylon.PylonBlockEntity;
import com.joelcrosby.fluxpylons.pylon.network.graph.PylonGraphNodeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class FluxPylonsBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES_REGISTRY = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, FluxPylons.ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeBlockEntity>> BASIC_PIPE = BLOCK_ENTITIES_REGISTRY
            .register("pipe", () -> BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.BASIC), FluxPylonsBlocks.BASIC_PIPE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeBlockEntity>> ADV_PIPE = BLOCK_ENTITIES_REGISTRY
            .register("adv_pipe", () -> BlockEntityType.Builder.of((pos, state) -> new PipeBlockEntity(pos, state, PipeType.BASIC), FluxPylonsBlocks.ADV_PIPE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CrateBlockEntity>> CRATE = BLOCK_ENTITIES_REGISTRY
            .register("crate", () -> BlockEntityType.Builder.of(CrateBlockEntity::new, FluxPylonsBlocks.CRATE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PylonBlockEntity>> PYLON = BLOCK_ENTITIES_REGISTRY
            .register("pylon", () -> BlockEntityType.Builder.of((pos, state) -> new PylonBlockEntity(pos, state, PylonGraphNodeType.BASIC), FluxPylonsBlocks.PYLON.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SmelterBlockEntity>> SMELTER = BLOCK_ENTITIES_REGISTRY
            .register("smelter", () -> BlockEntityType.Builder.of(SmelterBlockEntity::new, FluxPylonsBlocks.SMELTER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WasherBlockEntity>> WASHER = BLOCK_ENTITIES_REGISTRY
            .register("washer", () -> BlockEntityType.Builder.of(WasherBlockEntity::new, FluxPylonsBlocks.WASHER.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BoilerBlockEntity>> BOILER = BLOCK_ENTITIES_REGISTRY
            .register("boiler", () -> BlockEntityType.Builder.of(BoilerBlockEntity::new, FluxPylonsBlocks.BOILER.get()).build(null));

}
