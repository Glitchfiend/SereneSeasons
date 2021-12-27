/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.config;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.function.Predicate;

public class SeasonsConfig
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    // Weather settings
    public static ForgeConfigSpec.BooleanValue generateSnowAndIce;
    public static ForgeConfigSpec.BooleanValue changeWeatherFrequency;

    // Aesthetic settings
    public static ForgeConfigSpec.BooleanValue changeGrassColor;
    public static ForgeConfigSpec.BooleanValue changeFoliageColor;
    public static ForgeConfigSpec.BooleanValue changeBirchColor;

    private static List<String> defaultWhitelistedDimensions = Lists.newArrayList(Level.OVERWORLD.location().toString());
    private static final Predicate<Object> DIMENSION_VALIDATOR = (obj) ->
    {
        if (!(obj instanceof String))
            return false;

        try
        {
            new ResourceLocation((String)obj);
        }
        catch (Exception e)
        {
            // Can't convert to a resource location, therefore this object is invalid
            return false;
        }

        return true;
    };

    static
    {
        BUILDER.push("weather_settings");
        generateSnowAndIce = BUILDER.comment("Generate snow and ice during the Winter season").define("generate_snow_ice", true);
        changeWeatherFrequency = BUILDER.comment("Change the frequency of rain/snow/storms based on the season").define("change_weather_frequency", true);
        BUILDER.pop();

        BUILDER.push("aesthetic_settings");
        changeGrassColor = BUILDER.comment("Change the grass color based on the current season").define("change_grass_color", true);
        changeFoliageColor = BUILDER.comment("Change the foliage colour based on the current season").define("change_foliage_color", true);
        changeBirchColor = BUILDER.comment("Change the birch colour based on the current season").define("change_birch_color", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
