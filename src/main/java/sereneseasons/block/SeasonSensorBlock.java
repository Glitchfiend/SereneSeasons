/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;
import sereneseasons.tileentity.SeasonSensorTileEntity;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class SeasonSensorBlock extends BaseEntityBlock
{
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    private static final IntegerProperty SEASON = IntegerProperty.create("season", 0, 3);

    public SeasonSensorBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0).setValue(SEASON, 0));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext selectionContext)
    {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    public int getSignal(BlockState state, BlockGetter reader, BlockPos pos, Direction direction)
    {
        return state.getValue(POWER);
    }

    public void updatePower(Level world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);

        if (SeasonsConfig.isDimensionWhitelisted(world.dimension()))
        {
            BlockState currentState = world.getBlockState(pos);

            int power = 0;
            int startTicks = currentState.getValue(SEASON) * SeasonTime.ZERO.getSeasonDuration();
            int endTicks = (currentState.getValue(SEASON) + 1) * SeasonTime.ZERO.getSeasonDuration();
            int currentTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();

            if (currentTicks >= startTicks && currentTicks <= endTicks)
            {
                float delta = (float)(currentTicks - startTicks) / (float)SeasonTime.ZERO.getSeasonDuration();
                power = (int)Math.min(delta * 15.0F + 1.0F, 15.0F);
            }

            //Only update the state if the power level has actually changed
            if ((currentState.getValue(POWER)).intValue() != power)
            {
                world.setBlock(pos, currentState.setValue(POWER, Integer.valueOf(power)), 3);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult rayTraceResult)
    {
        if (player.mayBuild())
        {
            if (world.isClientSide)
            {
                return InteractionResult.SUCCESS;
            }
            else
            {
                BlockState blockstate = state.cycle(SEASON);
                world.setBlock(pos, blockstate, 4);
                updatePower(world, pos);
                return InteractionResult.SUCCESS;
            }
        }
        else
        {
            return super.use(state, world, pos, player, hand, rayTraceResult);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockGetter reader)
    {
        return new SeasonSensorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(POWER, SEASON);
    }
}
