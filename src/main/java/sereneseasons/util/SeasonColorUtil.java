/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.util;


import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.Biome;

import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.Season;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.init.ModConfig;

import java.util.List;

public class SeasonColorUtil
{
    public static int multiplyColours(int colour1, int colour2)
    {
        //Convert each colour to a scale between 0 and 1 and multiply them
        //Multiply by 255 to bring back between 0 and 255
        return (int)((colour1 / 255.0F) * (colour2 / 255.0F) * 255.0F);
    }

    public static int overlayBlendChannel(int underColour, int overColour)
    {
        int retVal;
        if (underColour < 128)
        {
            retVal = multiplyColours(2 * underColour, overColour);
        }
        else
        {
            retVal = multiplyColours(2 * (255 - underColour), 255 - overColour);
            retVal = 255 - retVal;
        }
        return retVal;
    }
    
    public static int overlayBlend(int underColour, int overColour)
    {
        int r = overlayBlendChannel((underColour >> 16) & 255, (overColour >> 16) & 255);
        int g = overlayBlendChannel((underColour >> 8) & 255, (overColour >> 8) & 255);
        int b = overlayBlendChannel(underColour & 255, overColour & 255);
        
        return (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }
    
    public static int saturateColour(int colour, float saturationMultiplier)
    {
        Color newColor = new Color(colour);
        double[] hsv = newColor.toHSV();
        hsv[1] *= saturationMultiplier;
        newColor = Color.convertHSVtoRGB(hsv[0], hsv[1], hsv[2]);
        return newColor.toInt();
    }
    
    public static int applySeasonalGrassColouring(ISeasonColorProvider colorProvider, Biome biome, int originalColour)
    {
        int dimensionId =  Minecraft.getInstance().level.getDimension().getType().getId();
        if (!BiomeConfig.enablesSeasonalEffects(biome) || !SeasonsConfig.isDimensionWhitelisted(dimensionId))
            return originalColour;

        int overlay = colorProvider.getGrassOverlay();
        float saturationMultiplier = colorProvider.getGrassSaturationMultiplier();
        if (!SeasonsConfig.changeGrassColor.get())
    	{
        	overlay = Season.SubSeason.MID_SUMMER.getGrassOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getGrassSaturationMultiplier();
    	}
        int newColour = overlay == 0xFFFFFF ? originalColour : overlayBlend(originalColour, overlay);
        return saturationMultiplier != -1 ? saturateColour(newColour, saturationMultiplier) : newColour;
    }
    
    public static int applySeasonalFoliageColouring(ISeasonColorProvider colorProvider, Biome biome, int originalColour)
    {
        int dimensionId =  Minecraft.getInstance().level.getDimension().getType().getId();
        if (!BiomeConfig.enablesSeasonalEffects(biome) || !SeasonsConfig.isDimensionWhitelisted(dimensionId))
            return originalColour;

        int overlay = colorProvider.getFoliageOverlay();
        float saturationMultiplier = colorProvider.getFoliageSaturationMultiplier();
        if (!SeasonsConfig.changeFoliageColor.get())
    	{
        	overlay = Season.SubSeason.MID_SUMMER.getFoliageOverlay();
            saturationMultiplier = Season.SubSeason.MID_SUMMER.getFoliageSaturationMultiplier();
    	}
        int newColour = overlay == 0xFFFFFF ? originalColour : overlayBlend(originalColour, overlay);
        return saturationMultiplier != -1 ? saturateColour(newColour, saturationMultiplier) : newColour;
    }
}
