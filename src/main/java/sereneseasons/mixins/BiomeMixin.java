package sereneseasons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import sereneseasons.asm.IBiomeMixin;
import sereneseasons.season.SeasonASMHelper;

@Mixin(BiomeGenBase.class)
public abstract class BiomeMixin implements IBiomeMixin
{
    @Shadow
    public boolean enableRain;

    @Shadow
    public abstract boolean func_150559_j();

    @Shadow
    protected static NoiseGeneratorPerlin temperatureNoise;

    @Shadow
    public float temperature;

    public boolean canSpawnLightningBoltOld()
    {
        // Overridden methods provided by WeatherTransformer
        return this.func_150559_j() ? false : this.enableRain;
    }

    public boolean getEnableSnowOld()
    {
        // Overridden methods provided by WeatherTransformer
        return this.func_150559_j();
    }

    public float getFloatTemperatureOld(int x, int y, int z)
    {
        // Overridden methods provided by WeatherTransformer
        if (y > 64)
        {
            float f = (float) temperatureNoise.func_151601_a((double) x * 1.0D / 8.0D, (double) z * 1.0D / 8.0D) * 4.0F;
            return this.temperature - (f + (float) y - 64.0F) * 0.05F / 30.0F;
        }
        else
        {
            return this.temperature;
        }
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    public boolean canSpawnLightningBolt()
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return canSpawnLightningBoltOld();
        }
        return SeasonASMHelper.shouldAddRainParticles(world, (BiomeGenBase) (Object) this);
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    public boolean getEnableSnow()
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return getEnableSnowOld();
        }
        return SeasonASMHelper.shouldRenderRainSnow(world, (BiomeGenBase) (Object) this);
    }

    /**
     * @author darkshadow44
     * @reason Redirect to our coloring handlers.
     */
    @Overwrite
    public float getFloatTemperature(int x, int y, int z)
    {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null)
        {
            return getFloatTemperatureOld(x, y, z);
        }
        return SeasonASMHelper.getFloatTemperature(Minecraft.getMinecraft().theWorld, (BiomeGenBase) (Object) this, x, y, z);
    }
}
