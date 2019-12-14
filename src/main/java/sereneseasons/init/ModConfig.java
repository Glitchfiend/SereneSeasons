/*******************************************************************************
 * Copyright 2014-2017, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import sereneseasons.api.config.ISyncedOption;
import sereneseasons.api.config.SeasonsOption;
import sereneseasons.api.config.SyncedConfig;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.FertilityConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.core.SereneSeasons;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModConfig
{
    public static void init()
    {
        Path configPath = FMLPaths.CONFIGDIR.get();
        Path modConfigPath = Paths.get(configPath.toAbsolutePath().toString(), "sereneseasons");

        try
        {
            Files.createDirectory(modConfigPath);
        }
        catch (FileAlreadyExistsException e) {}
        catch (IOException e)
        {
            SereneSeasons.logger.error("Failed to create sereneseasons config directory", e);
        }

        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, FertilityConfig.SPEC, "sereneseasons/fertility.toml");
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, SeasonsConfig.SPEC, "sereneseasons/seasons.toml");

        BiomeConfig.init(modConfigPath.toFile());
    }

    public static void setup()
    {
        addSyncedValue(SeasonsOption.DAY_DURATION, SeasonsConfig.dayDuration.get());
        addSyncedValue(SeasonsOption.SUB_SEASON_DURATION, SeasonsConfig.subSeasonDuration.get());
        addSyncedValue(SeasonsOption.STARTING_SUB_SEASON, SeasonsConfig.startingSubSeason.get());
        addSyncedValue(SeasonsOption.PROGRESS_SEASON_WHILE_OFFLINE, SeasonsConfig.progressSeasonWhileOffline.get());
    }

    private static <T> void addSyncedValue(ISyncedOption option, T defaultValue)
    {
        SyncedConfig.addOption(option, defaultValue.toString());
    }
}
