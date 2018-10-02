package sereneseasons.handler.season;

import javax.annotation.Nullable;

import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.config.BiomeConfig;
import sereneseasons.init.ModConfig;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.WorldUtils;

public class BirchColorHandler
{
	public static void init()
    {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor()
	    {
	        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex)
	        {
	            BlockPlanks.EnumType plankstype = (BlockPlanks.EnumType)state.getValue(BlockOldLeaf.VARIANT);
	            
	            if (plankstype == BlockPlanks.EnumType.SPRUCE)
	            {
	                return ColorizerFoliage.getFoliageColorPine();
	            }
	            else if (plankstype == BlockPlanks.EnumType.BIRCH)
	            {
	            	int birchColor = ColorizerFoliage.getFoliageColorBirch();
	            	
	            	if (worldIn != null && pos != null && ModConfig.seasons.changeBirchColour)
	            	{
	            		if( !getSafeIsWorldBlacklisted(worldIn) ) {
		            		SeasonTime calendar = new SeasonTime(SeasonHandler.clientSeasonCycleTicks);
			                ISeasonColorProvider colorProvider = BiomeConfig.usesTropicalSeasons(worldIn.getBiome(pos)) ? calendar.getTropicalSeason() : calendar.getSubSeason();
			                birchColor = colorProvider.getBirchColor();
			            }
	            	}
	            	
	                return birchColor;
	            }
	            else
	            {
	                return worldIn != null && pos != null ? BiomeColorHelper.getFoliageColorAtPos(worldIn, pos) : ColorizerFoliage.getFoliageColorBasic();
	            }
	        }
	    }, Blocks.LEAVES);
    }
	
	private static boolean getSafeIsWorldBlacklisted(IBlockAccess blockAccess) {
		if( blockAccess == null )
			return false;
		
		World world = WorldUtils.castToWorld(blockAccess);
		if( world != null ) {
			int dimId = world.provider.getDimension();
			return SeasonHandler.isDimensionBlacklisted(dimId);
		}
		
		return false;
	}

}
