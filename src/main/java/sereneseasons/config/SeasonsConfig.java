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

    static
    {
        BUILDER.comment("Please be advised that certain season-related options are world-specific and are located in <Path to your world folder>/serverconfig/sereneseasons-server.toml.");
        BUILDER.push("weather_settings");
        generateSnowAndIce = BUILDER.comment("Generate snow and ice during the Winter season").define("generate_snow_ice", true);
        changeWeatherFrequency = BUILDER.comment("Change the frequency of rain/snow/storms based on the season").define("change_weather_frequency", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}