package sereneseasons.core;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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
        ItemModelsProperties.register(SSItems.calendar, new ResourceLocation("time"), new IItemPropertyGetter()
        {
            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, ClientWorld clientWorld, LivingEntity entity)
            {
                World world = clientWorld;
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

                    return MathHelper.positiveModulo((float)d0, 1.0F);
                }
            }
        });

        ItemModelsProperties.register(SSItems.calendar, new ResourceLocation("seasontype"), new IItemPropertyGetter()
        {
            @Override
            @OnlyIn(Dist.CLIENT)
            public float call(ItemStack stack, ClientWorld clientWorld, LivingEntity entity)
            {
                World world = clientWorld;
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
                            RegistryKey<Biome> biome = world.getBiomeName(holder.blockPosition()).orElse(null);

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
