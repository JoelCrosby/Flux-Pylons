package com.joelcrosby.fluxpylons.pipe;

import com.google.common.collect.ImmutableMap;
import com.joelcrosby.fluxpylons.FluxPylons;
import com.joelcrosby.fluxpylons.FluxPylonsCapabilities;
import com.joelcrosby.fluxpylons.Utility;
import com.joelcrosby.fluxpylons.item.WrenchItem;
import com.joelcrosby.fluxpylons.item.upgrade.UpgradeItem;
import com.joelcrosby.fluxpylons.pipe.network.NetworkManager;
import com.joelcrosby.fluxpylons.setup.Common;
import com.joelcrosby.fluxpylons.util.Raytracer;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class PipeBlock extends Block implements EntityBlock {
    public MapCodec<PipeBlock> codec;

    public static final Map<Direction, EnumProperty<ConnectionType>> DIRECTIONS = new HashMap<>();

    private static final Map<Pair<BlockState, BlockState>, VoxelShape> SHAPE_CACHE = new HashMap<>();
    private static final Map<Pair<BlockState, BlockState>, VoxelShape> COLL_SHAPE_CACHE = new HashMap<>();

    private static final VoxelShape CENTER_SHAPE = box(5, 5, 5, 11, 11, 11);

    public static final Map<Direction, VoxelShape> DIR_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(5, 10, 5, 11, 16, 11))
            .put(Direction.DOWN, box(5, 0, 5, 11, 6, 11))
            .put(Direction.NORTH, box(5, 5, 0, 11, 11, 6))
            .put(Direction.SOUTH, box(5, 5, 10, 11, 11, 16))
            .put(Direction.EAST, box(10, 5, 5, 16, 11, 11))
            .put(Direction.WEST, box(0, 5, 5, 6, 11, 11))
            .build();

    public static final Map<Direction, VoxelShape> DIR_SHAPES_END = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, box(4, 11, 4, 12, 16, 12))
            .put(Direction.DOWN, box(4, 0, 4, 12, 5, 12))
            .put(Direction.NORTH, box(4, 4, 0, 12, 12, 5))
            .put(Direction.SOUTH, box(4, 4, 11, 12, 12, 16))
            .put(Direction.EAST, box(11, 4, 4, 16, 12, 12))
            .put(Direction.WEST, box(0, 4, 4, 5, 12, 12))
            .build();

    public static final Map<Direction, VoxelShape> DIR_SHAPES_END_ADV = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, Shapes.join(box(4, 13, 4, 12, 16, 12), box(6, 11, 6, 10, 13, 10), BooleanOp.OR))
            .put(Direction.DOWN, Shapes.join(box(4, 0, 4, 12, 3, 12), box(6, 3, 6, 10, 5, 10), BooleanOp.OR))
            .put(Direction.NORTH, Shapes.join(box(4, 4, 0, 12, 12, 3), box(6, 6, 3, 10, 10, 5), BooleanOp.OR))
            .put(Direction.SOUTH, Shapes.join(box(4, 4, 13, 12, 12, 16), box(6, 6, 11, 10, 10, 13), BooleanOp.OR))
            .put(Direction.EAST, Shapes.join(box(13, 4, 4, 16, 12, 12), box(11, 6, 6, 13, 10, 10), BooleanOp.OR))
            .put(Direction.WEST, Shapes.join(box(0, 4, 4, 3, 12, 12), box(3, 6, 6, 5, 10, 10), BooleanOp.OR))
            .build();

    static {
        for (var dir : Direction.values())
            DIRECTIONS.put(dir, EnumProperty.create(dir.getName(), ConnectionType.class));
    }

    private final PipeType pipeType;

    public PipeBlock(Properties properties, PipeType pipeType) {
        super(properties.strength(1.6f).sound(SoundType.COPPER));

        this.pipeType = pipeType;
        this.codec = BlockBehaviour.simpleCodec((p) -> new PipeBlock(p, pipeType));

        var state = this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false);

        for (var prop : DIRECTIONS.values()) {
            state = state.setValue(prop, ConnectionType.DISCONNECTED);
        }

        this.registerDefaultState(state);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, BlockHitResult result) {
        var dir = getPipeEndDirectionClicked(pos, result.getLocation());
        var entity = level.getBlockEntity(pos);

        if (dir == null || entity == null || !(state.getBlock() instanceof PipeBlock)) {
            return InteractionResult.FAIL;
        }

        var connectionType =  state.getValue(DIRECTIONS.get(dir));

        if (!connectionType.isEnd()) {
            return InteractionResult.FAIL;
        }

        if (!(entity instanceof PipeBlockEntity pipeBlockEntity)) {
            return InteractionResult.FAIL;
        }

        var itemStack = player.getItemInHand(player.getUsedItemHand());

        if (!player.isCrouching() && itemStack.getItem() instanceof UpgradeItem) {
            if (!level.isClientSide) {
                if (pipeBlockEntity.getUpgradeManager(dir).insertUpgrade(itemStack.copy())) {
                    itemStack.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!player.isCrouching() && itemStack.getItem() instanceof WrenchItem) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            pipeBlockEntity.getUpgradeManager(dir).OpenContainerMenu(serverPlayer);
            return InteractionResult.sidedSuccess(false);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        var tile = Utility.getBlockEntity(PipeBlockEntity.class, level, pos);
        if (tile != null && level instanceof ServerLevel) {
            for (var dir : Direction.values()) {
                tile.getUpgradeManager(dir).dropContents(level, pos);
            }

            if (tile.cover != null)
                tile.removeCover(player, InteractionHand.MAIN_HAND);
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new PipeBlockEntity(pos, state, pipeType);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block blockIn, @NotNull BlockPos fromPos, boolean isMoving) {
        updateState(state, level, pos);

        if (!level.isClientSide) {
            var pipe = NetworkManager.get(level).getNode(pos);

            if (pipe != null && pipe.getNetwork() != null) {
                pipe.getNetwork().scanGraph((ServerLevel) level, pos);
            }
        }
    }

    public void updateState(BlockState state, Level level, BlockPos pos) {
        var newState = this.createState(level, pos, state);
        if (newState != state) {
            level.setBlockAndUpdate(pos, newState);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTIONS.values().toArray(new EnumProperty[0]));
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.createState(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
    }

    @Override
    public @NotNull BlockState updateShape(BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if (stateIn.getValue(BlockStateProperties.WATERLOGGED))
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));
        return createState((Level) worldIn, currentPos, stateIn);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos, s -> s.getShape(worldIn, pos, context), SHAPE_CACHE, null);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos,  s -> s.getCollisionShape(worldIn, pos, context), COLL_SHAPE_CACHE, s -> {
            // make the shape a bit higher to allow player to jump up onto a higher block
            var newShape = new MutableObject<>(Shapes.empty());
            s.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShape.setValue(Shapes.join(Shapes.create(x1, y1, z1, x2, y2 + 3 / 16F, z2), newShape.getValue(), BooleanOp.OR)));
            return newShape.getValue().optimize();
        });
    }

    private VoxelShape cacheAndGetShape(BlockState state,
                                        BlockGetter worldIn,
                                        BlockPos pos,
                                        Function<BlockState, VoxelShape> coverShapeSelector,
                                        Map<Pair<BlockState, BlockState>, VoxelShape> cache,
                                        Function<VoxelShape, VoxelShape> shapeModifier) {
        VoxelShape coverShape = null;
        BlockState cover = null;

        var tile = Utility.getBlockEntity(PipeBlockEntity.class, worldIn, pos);
        if (tile != null && tile.cover != null) {
            cover = tile.cover;
            // try catch since the block might expect to find itself at the position
            try {
                coverShape = coverShapeSelector.apply(cover);
            } catch (Exception ignored) {
            }
        }

        var key = Pair.of(state, cover);
        var shape = cache.get(key);

        if (shape == null) {
            shape = CENTER_SHAPE;

            for (var entry : DIRECTIONS.entrySet()) {
                var connectionType = state.getValue(entry.getValue());

                if (connectionType.isEnd()) {
                    if (pipeType == PipeType.BASIC) {
                        shape = Shapes.or(shape, DIR_SHAPES_END.get(entry.getKey()));
                    } else {
                        shape = Shapes.or(shape, DIR_SHAPES_END_ADV.get(entry.getKey()));
                    }
                } else if (connectionType.isConnected()) {
                    shape = Shapes.or(shape, DIR_SHAPES.get(entry.getKey()));
                }
            }

            if (shapeModifier != null) {
                shape = shapeModifier.apply(shape);
            }

            if (coverShape != null) {
                shape = Shapes.or(shape, coverShape);
            }

            cache.put(key, shape);
        }

        return shape;
    }

    private BlockState createState(Level world, BlockPos pos, BlockState curr) {
        var state = this.defaultBlockState();
        var fluid = world.getFluidState(pos);

        if (fluid.is(FluidTags.WATER) && fluid.getAmount() == 8) {
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);
        }

        for (var dir : Direction.values()) {
            var prop = DIRECTIONS.get(dir);
            var type = this.getConnectionType(world, pos, dir);
            // don't reconnect on blocked faces
            if (type.isConnected() && curr.getValue(prop) == ConnectionType.BLOCKED) {
                type = ConnectionType.BLOCKED;
            }

            state = state.setValue(prop, type);
        }

        return state;
    }

    public ConnectionType getConnectionType(Level world, BlockPos pos, Direction direction) {
        var offset = pos.relative(direction);

        // TODO: Determine if below is needed

        if (!world.isLoaded(offset))
            return ConnectionType.DISCONNECTED;

        var opposite = direction.getOpposite();
        var tile = world.getBlockEntity(offset);



        if (tile != null) {
            // TODO: Implement a cleaner way of ensuring mismatched types don't connect
            if (tile instanceof PipeBlockEntity) {
                if (((PipeBlockEntity) tile).getPipeType() != pipeType) {
                    return ConnectionType.DISCONNECTED;
                }
            }

            var connectable = world.getCapability(FluxPylonsCapabilities.PipeConnectableCapability, pos.relative(direction), direction.getOpposite());
            if (connectable != null) {
                return connectable.getConnectionType(pos, direction);
            }

            var itemHandler = world.getCapability(Capabilities.ItemHandler.BLOCK, pos.relative(direction), direction.getOpposite());
            if (itemHandler != null) {
                return ConnectionType.END;
            }

            var energyHandler = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (energyHandler != null) {
                return ConnectionType.END;
            }

            var fluidHandler = world.getCapability(Capabilities.FluidHandler.BLOCK, pos.relative(direction), direction.getOpposite());
            if (fluidHandler != null) {
                return ConnectionType.END;
            }
        }

        var blockHandler = Utility.getBlockItemHandler(world, offset, opposite);
        if (blockHandler != null) {
            return ConnectionType.END;
        }


        return ConnectionType.DISCONNECTED;
    }

    @Nullable
    public Direction getPipeEndDirectionClicked(BlockPos pos, Vec3 hit) {
        for (var dir : Direction.values()) {
            if (Raytracer.inclusiveContains(DIR_SHAPES_END.get(dir).bounds().move(pos), hit)) {
                return dir;
            }
        }

        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        var energyRate = this.pipeType.getNodeType().getEnergyTransferRate();
        var fluidRate = this.pipeType.getNodeType().getFluidTransferRate();
        var itemRate = this.pipeType.getNodeType().getItemTransferRate();

        var formatter = new DecimalFormat("#,###");

        var energyRateText = formatter.format(energyRate) + " FE/t";
        var fluidRateText = fluidRate + " MB/t";
        var itemRateText = itemRate + " /0.5s";

        var energyText = I18n.get("terms." + FluxPylons.ID + ".energy").concat(" ");
        var fluidText = I18n.get("terms." + FluxPylons.ID + ".fluids").concat(" ");
        var itemText = I18n.get("terms." + FluxPylons.ID + ".items").concat(" ");

        var energyComponent = Component.translatable(energyText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(energyRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        var fluidComponent = Component.translatable(fluidText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(fluidRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        var itemComponent = Component.translatable(itemText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_PURPLE))
                .append(Component.translatable(itemRateText).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));

        tooltipComponents.addAll(Arrays.asList(energyComponent, fluidComponent, itemComponent));

        Utility.addTooltip(BuiltInRegistries.BLOCK.getKey(this).getPath(), tooltipComponents);
    }
}
