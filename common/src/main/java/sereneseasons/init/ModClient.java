/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import glitchcore.event.EventManager;
import glitchcore.event.client.ItemTooltipEvent;
import glitchcore.event.client.RegisterColorsEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import sereneseasons.api.SSItems;
import sereneseasons.api.season.ISeasonColorProvider;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonColorHandlers;
import sereneseasons.season.SeasonHandlerClient;
import sereneseasons.season.SeasonTime;
import sereneseasons.util.SeasonColorUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

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
        EventManager.addListener(ModClient::onItemTooltip);
    }

    public static void onItemTooltip(ItemTooltipEvent event)
    {
        List<Component> tooltip = event.getTooltip();
        Player player = event.getPlayer();

        if (event.getStack().getItem() != SSItems.CALENDAR)
            return;

        if (player != null && ModConfig.seasons.isDimensionWhitelisted(player.level().dimension()))
        {
            int seasonCycleTicks = SeasonHelper.getSeasonState(player.level()).getSeasonCycleTicks();
            SeasonTime time = new SeasonTime(seasonCycleTicks);
            int subSeasonDuration = ModConfig.seasons.subSeasonDuration;

            tooltip.add(Component.translatable("desc.sereneseasons." + time.getSubSeason().toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY).append(Component.literal(" (").withStyle(ChatFormatting.DARK_GRAY)).append(Component.translatable("desc.sereneseasons." + time.getTropicalSeason().toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.DARK_GRAY)).append(Component.literal(")").withStyle(ChatFormatting.DARK_GRAY)));
            tooltip.add(Component.translatable("desc.sereneseasons.day_counter", (time.getDay() % subSeasonDuration) + 1, subSeasonDuration).withStyle(ChatFormatting.GRAY).append(Component.translatable("desc.sereneseasons.tropical_day_counter", (((time.getDay() + subSeasonDuration) % (subSeasonDuration * 2)) + 1), subSeasonDuration * 2).withStyle(ChatFormatting.DARK_GRAY)));
        }
        else
        {
            tooltip.add(Component.literal("???").withStyle(ChatFormatting.GRAY));
        }
    }

    public static void registerItemProperties()
    {
//        ItemProperties.register(SSItems.CALENDAR, Identifier.fromNamespaceAndPath(SereneSeasons.MOD_ID, "time"), new ClampedItemPropertyFunction()
//        {
//            @Override
//            public float unclampedCall(ItemStack stack, ClientLevel clientWorld, LivingEntity entity, int seed)
//            {
//                Level world = clientWorld;
//                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());
//
//                if (world == null && holder != null)
//                {
//                    world = holder.level();
//                }
//
//                if (world == null)
//                {
//                    return 0.0F;
//                }
//                else
//                {
//                    double d0;
//
//                    int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
//                    d0 = (double)((float)seasonCycleTicks / (float) SeasonTime.ZERO.getCycleDuration());
//
//                    return Mth.positiveModulo((float)d0, 1.0F);
//                }
//            }
//        });
//
//        ItemProperties.register(SSItems.CALENDAR, Identifier.fromNamespaceAndPath(SereneSeasons.MOD_ID, "seasontype"), new ClampedItemPropertyFunction()
//        {
//            @Override
//            public float unclampedCall(ItemStack stack, ClientLevel clientWorld, LivingEntity entity, int seed)
//            {
//                Level level = clientWorld;
//                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());
//
//                if (level == null && holder != null)
//                {
//                    level = holder.level();
//                }
//
//                if (level == null)
//                {
//                    return 2.0F;
//                }
//                else
//                {
//                    float type;
//
//                    if (ModConfig.seasons.isDimensionWhitelisted(level.dimension()))
//                    {
//                        if (holder != null)
//                        {
//                            Holder<Biome> biome = level.getBiome(holder.blockPosition());
//
//                            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES))
//                            {
//                                type = 1.0F;
//                            }
//                            else
//                            {
//                                type = 0.0F;
//                            }
//                        }
//                        else
//                        {
//                            type = 0.0F;
//                        }
//                    }
//                    else
//                    {
//                        type = 2.0F;
//                    }
//
//                    return type;
//                }
//            }
//        });
    }

    private static void registerBlockColors(RegisterColorsEvent.Block event)
    {
        event.register(List.of(new BlockTintSource() {
            @Override
            public int color(BlockState blockState)
            {
                return FoliageColor.FOLIAGE_BIRCH;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
                int birchColor = FoliageColor.FOLIAGE_BIRCH;
                Level level = Minecraft.getInstance().player.level();
                ResourceKey<Level> dimension = level.dimension();

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
                            birchColor = SeasonColorUtil.mixColours(colorProvider.getBirchColor(), FoliageColor.FOLIAGE_BIRCH, 0.75F);
                        }
                    }
                }

                // Ensure the upper bits are set for opacity
                return birchColor | (0xFF << 24);
            }
        }), Blocks.BIRCH_LEAVES);
    }
}
