package sereneseasons.core;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import sereneseasons.api.SSItems;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.BiomeConfig;
import sereneseasons.config.SeasonsConfig;
import sereneseasons.season.SeasonTime;

public class ClientProxy extends CommonProxy
{
    @OnlyIn(Dist.CLIENT)
    @Override
    void registerItemModelsProperties()
    {
        ItemProperties.register(SSItems.calendar, new ResourceLocation("time"), new ItemPropertyFunction()
        {
            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, ClientLevel clientWorld, LivingEntity entity)
            {
                Level world = clientWorld;
                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());

                if (world == null && holder != null)
                {
                    world = holder.level;
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

        ItemProperties.register(SSItems.calendar, new ResourceLocation("seasontype"), new ItemPropertyFunction()
        {
            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, ClientLevel clientWorld, LivingEntity entity)
            {
                Level world = clientWorld;
                Entity holder = (Entity)(entity != null ? entity : stack.getFrame());

                if (world == null && holder != null)
                {
                    world = holder.level;
                }

                if (world == null)
                {
                    return 2.0F;
                }
                else
                {
                    float type;

                    if (SeasonsConfig.isDimensionWhitelisted(world.dimension()))
                    {
                        if (holder != null)
                        {
                            ResourceKey<Biome> biome = world.getBiomeName(holder.blockPosition()).orElse(null);

                            if (BiomeConfig.usesTropicalSeasons(biome))
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
}
