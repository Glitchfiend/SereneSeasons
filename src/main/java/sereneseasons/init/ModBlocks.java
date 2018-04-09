package sereneseasons.init;

import static sereneseasons.api.SSBlocks.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import sereneseasons.api.ISSBlock;
import sereneseasons.block.BlockSeasonSensor;
import sereneseasons.core.SereneSeasons;
import sereneseasons.tileentity.TileEntitySeasonSensor;
import sereneseasons.util.BlockStateUtils;
import sereneseasons.util.inventory.CreativeTabSS;

public class ModBlocks
{
    public static void init()
    {
        season_sensors[0] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SPRING), "season_sensor_spring" );
        season_sensors[1] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.SUMMER), "season_sensor_summer" ).setCreativeTab(null);
        season_sensors[2] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.AUTUMN), "season_sensor_autumn" ).setCreativeTab(null);
        season_sensors[3] = registerBlock( new BlockSeasonSensor(BlockSeasonSensor.DetectorType.WINTER), "season_sensor_winter" ).setCreativeTab(null);

        GameRegistry.registerTileEntity(TileEntitySeasonSensor.class, "season_sensor");
    }

    public static void registerBlockItemModel(Block block, String stateName, int stateMeta)
    {
        Item item = Item.getItemFromBlock(block);
        SereneSeasons.proxy.registerItemVariantModel(item, stateName, stateMeta);
    }

    public static Block registerBlock(Block block, String blockName)
    {
        // by default, set the creative tab for all blocks added in BOP to CreativeTabBOP.instance
        return registerBlock(block, blockName, CreativeTabSS.instance);
    }

    public static Block registerBlock(Block block, String blockName,CreativeTabs tab)
    {
        return registerBlock(block, blockName, tab, true);
    }

    public static Block registerBlock(Block block, String blockName, CreativeTabs tab, boolean registerItemModels)
    {
        Preconditions.checkNotNull(block, "Cannot register a null block");
        block.setUnlocalizedName(blockName);
        block.setCreativeTab(tab);

        if (block instanceof ISSBlock)
        {
            // if this block supports the IBOPBlock interface then we can determine the item block class, and sub-blocks automatically
            ISSBlock bopBlock = (ISSBlock) block;

            registerBlockWithItem(block, blockName, bopBlock.getItemClass());
            SereneSeasons.proxy.registerBlockSided(block);

            // check for missing default states
            IBlockState defaultState = block.getDefaultState();
            if (defaultState == null)
            {
                defaultState = block.getBlockState().getBaseState();
                SereneSeasons.logger.error("Missing default state for " + block.getUnlocalizedName());
            }

            // Some blocks such as doors and slabs register their items after the blocks (getItemClass returns null)
            if (registerItemModels)
            {
                // get the preset blocks variants
                ImmutableSet<IBlockState> presets = BlockStateUtils.getBlockPresets(block);
                if (presets.isEmpty())
                {
                    // block has no sub-blocks to register
                    registerBlockItemModel(block, blockName, 0);
                } else
                {
                    // register all the sub-blocks
                    for (IBlockState state : presets)
                    {
                        String stateName = bopBlock.getStateName(state);
                        int stateMeta = block.getMetaFromState(state);
                        registerBlockItemModel(block, stateName, stateMeta);
                    }
                }
            }
        }
        else
        {
            // for vanilla blocks, just register a single variant with meta=0 and assume ItemBlock for the item class
            registerBlockWithItem(block, blockName, ItemBlock.class);
            registerBlockItemModel(block, blockName, 0);
        }

        return block;
    }

    private static void registerBlockWithItem(Block block, String blockName, Class<? extends ItemBlock> clazz)
    {
        try
        {
            Item itemBlock = clazz != null ? (Item)clazz.getConstructor(Block.class).newInstance(block) : null;
            ResourceLocation location = new ResourceLocation(SereneSeasons.MOD_ID, blockName);

            block.setRegistryName(new ResourceLocation(SereneSeasons.MOD_ID, blockName));

            ForgeRegistries.BLOCKS.register(block);
            if (itemBlock != null)
            {
                itemBlock.setRegistryName(new ResourceLocation(SereneSeasons.MOD_ID, blockName));
                ForgeRegistries.ITEMS.register(itemBlock);
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("An error occurred associating an item block during registration of " + blockName, e);
        }
    }
}
