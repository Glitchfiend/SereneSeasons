/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import sereneseasons.api.SSBlocks;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;
import sereneseasons.tileentity.SeasonSensorTileEntity;

public class SeasonSensorBlock extends ContainerBlock
{
    public static final IntegerProperty POWER;
    protected static final VoxelShape SHAPE;

    private final DetectorType type;

    public SeasonSensorBlock(Properties properties, DetectorType type)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(POWER, 0));
        this.type = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext selectionContext)
    {
        return SHAPE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader reader, BlockPos pos, Direction direction)
    {
        return state.getValue(POWER);
    }

    public void updatePower(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);

        if (SeasonsConfig.isDimensionWhitelisted(world.getDimension().getType().getId()))
        {
            BlockState currentState = world.getBlockState(pos);

            int power = 0;
            int startTicks = this.type.ordinal() * SeasonTime.ZERO.getSeasonDuration();
            int endTicks = (this.type.ordinal() + 1) * SeasonTime.ZERO.getSeasonDuration();
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
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult)
    {
        if (player.mayBuild())
        {
            if (world.isClientSide)
            {
                return ActionResultType.SUCCESS;
            }
            else
            {
                Block nextBlock = SSBlocks.season_sensors[(this.type.ordinal() + 1) % DetectorType.values().length];
                world.setBlock(pos, nextBlock.defaultBlockState().setValue(POWER, state.getValue(POWER)), 4);
                ((SeasonSensorBlock)nextBlock).updatePower(world, pos);
                return ActionResultType.SUCCESS;
            }
        }
        else
        {
            return super.use(state, world, pos, player, hand, rayTraceResult);
        }
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state)
    {
        return BlockRenderType.MODEL;
    }

    @Override
    public boolean isSignalSource(BlockState state)
    {
        return true;
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader reader)
    {
        return new SeasonSensorTileEntity();
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(new IProperty[]{POWER});
    }

    static
    {
        POWER = BlockStateProperties.POWER;
        SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);
    }

    public enum DetectorType implements IStringSerializable
    {
        SPRING, SUMMER, AUTUMN, WINTER;
        @Override
        public String getSerializedName()
        {
            return this.name().toLowerCase();
        }
        @Override
        public String toString()
        {
            return this.getSerializedName();
        }
    };
}
