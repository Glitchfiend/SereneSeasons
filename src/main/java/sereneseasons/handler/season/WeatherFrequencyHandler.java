/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.handler.season;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import sereneseasons.api.season.Season;
import sereneseasons.api.season.SeasonHelper;

public class WeatherFrequencyHandler 
{
    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event)
    {
        if (event.phase == Phase.END && event.side == Side.SERVER)
        {
            World world = event.world;
            int dimId = world.provider.getDimension();
            if( SeasonHandler.isDimensionBlacklisted(dimId) )
            	return;
            Season season = SeasonHelper.getSeasonState(world).getSubSeason().getSeason();
            
            //During winter, the absolute maximum delay between rain/thunder should be 30 mins, unlike
            //the standard 160 minutes. We only wish to modify the time between rain/thunder occurring, not
            //their duration.

            if (season == Season.SPRING)
            {
                if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 96000)
                {
                    world.getWorldInfo().setRainTime(world.rand.nextInt(84000) + 12000);
                }
            }
            else if (season == Season.SUMMER)
            {
                if (!world.getWorldInfo().isThundering() && world.getWorldInfo().getThunderTime() > 36000)
                {
                    world.getWorldInfo().setThunderTime(world.rand.nextInt(24000) + 12000);
                }
            }
            else if (season == Season.WINTER)
            {
            	if (world.getWorldInfo().isThundering())
                {
                    world.getWorldInfo().setThundering(false);;
                }
                if (!world.getWorldInfo().isRaining() && world.getWorldInfo().getRainTime() > 36000)
                {
                    world.getWorldInfo().setRainTime(world.rand.nextInt(24000) + 12000);
                }
            }
        }
    }
}
