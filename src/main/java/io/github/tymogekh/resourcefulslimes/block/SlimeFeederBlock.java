package io.github.tymogekh.resourcefulslimes.block;

import com.mojang.serialization.MapCodec;
import io.github.tymogekh.resourcefulslimes.blockentity.SlimeFeederBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlimeFeederBlock extends BaseEntityBlock {

    private static final MapCodec<SlimeFeederBlock> CODEC = simpleCodec(SlimeFeederBlock::new);
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public SlimeFeederBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FILLED, false));
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if(level.isClientSide()){
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof SlimeFeederBlockEntity){
                player.openMenu((MenuProvider) blockEntity, buf -> buf.writeBlockPos(pos));
            }
            return InteractionResult.CONSUME;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FILLED);
    }

    public static void changeBlockState(Level level, BlockState state, BlockPos pos){
        BlockState blockState = state.setValue(FILLED, true);
        level.setBlock(pos, blockState, 3);
        level.blockEntityChanged(pos);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new SlimeFeederBlockEntity(blockPos, blockState);
    }

}
