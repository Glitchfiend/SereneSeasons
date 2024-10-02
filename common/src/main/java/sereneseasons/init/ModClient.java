/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import glitchcore.event.EventManager;
import glitchcore.event.client.RegisterColorsEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.SSItems;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.core.SereneSeasons;
import sereneseasons.season.SeasonColorHandlers;
import sereneseasons.season.SeasonHandlerClient;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;

import javax.annotation.Nullable;

public class ModClient
{
    public static void setup()
    {
        SeasonColorHandlers.setup();
    }

    public static void addClientHandlers()
    {
        EventManager.addListener(SeasonHandlerClient::onClientTick);
        EventManager.addListener(ModFertility::setupTooltips);
        EventManager.addListener(ModClient::registerBlockColors);
    }

    public static void registerItemProperties()
    {
        ItemProperties.register(SSItems.CALENDAR, new ResourceLocation(SereneSeasons.MOD_ID, "time"), new ClampedItemPropertyFunction()
        {
            @Override
            public float unclampedCall(ItemStack stack, ClientLevel clientWorld, LivingEntity entity, int seed)
            {
                Level world = clientWorld;
                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());

                if (world == null && holder != null)
                {
                    world = holder.level();
                }

                if (world == null)
                {
                    return 0.0F;
                }
                else
                {
                    double d0;

                    int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
                    d0 = (double)((float)seasonCycleTicks / (float) SeasonTime.ZERO.getCycleDuration());

                    return Mth.positiveModulo((float)d0, 1.0F);
                }
            }
        });

        ItemProperties.register(SSItems.CALENDAR, new ResourceLocation(SereneSeasons.MOD_ID, "seasontype"), new ClampedItemPropertyFunction()
        {
            @Override
            public float unclampedCall(ItemStack stack, ClientLevel clientWorld, LivingEntity entity, int seed)
            {
                Level level = clientWorld;
                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());

                if (level == null && holder != null)
                {
                    level = holder.level();
                }

                if (level == null)
                {
                    return 2.0F;
                }
                else
                {
                    float type;

                    if (ModConfig.seasons.isDimensionWhitelisted(level.dimension()))
                    {
                        if (holder != null)
                        {
                            Holder<Biome> biome = level.getBiome(holder.blockPosition());

                            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES))
                            {
                                type = 1.0F;
                            }
                            else
                            {
                                type = 0.0F;
                            }
                        }
                        else
                        {
                            type = 0.0F;
                        }
                    }
                    else
                    {
                        type = 2.0F;
                    }

                    return type;
                }
            }
        });
    }

    private static void registerBlockColors(RegisterColorsEvent.Block event)
    {
        event.register((BlockState state, @Nullable BlockAndTintGetter dimensionReader, @Nullable BlockPos pos, int tintIndex) ->
        {
            int birchColor = FoliageColor.getBirchColor();
            Level level = Minecraft.getInstance().player.level();
            ResourceKey<Level> dimension = Minecraft.getInstance().player.level().dimension();

            if (level != null && pos != null && ModConfig.seasons.changeBirchColor && ModConfig.seasons.isDimensionWhitelisted(dimension))
            {
                Holder<Biome> biome = level.getBiome(pos);

                if (!biome.is(ModTags.Biomes.BLACKLISTED_BIOMES))
                {
                    ISeasonState calendar = SeasonHelper.getSeasonState(level);
                    ISeasonColorProvider colorProvider = biome.is(ModTags.Biomes.TROPICAL_BIOMES) ? calendar.getTropicalSeason() : calendar.getSubSeason();
                    birchColor = colorProvider.getBirchColor();

                    if (biome.is(ModTags.Biomes.LESSER_COLOR_CHANGE_BIOMES))
                    {
                        birchColor = SeasonColorUtil.mixColours(colorProvider.getBirchColor(), FoliageColor.getBirchColor(), 0.75F);
                    }
                }
            }

            return birchColor;
        }, Blocks.BIRCH_LEAVES);
    }
}
