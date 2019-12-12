package sereneseasons.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.World;
import sereneseasons.api.season.ISeasonState;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.season.SeasonASMHelper;

@Mixin(World.class)
public abstract class WorldMixin
{

    @Shadow
    public abstract boolean isRaining();

    @Shadow
    public abstract boolean canBlockSeeTheSky(int x, int y, int z);

    @Shadow
    public abstract int getPrecipitationHeight(int x, int z);

    /**
     * @author darkshadow44
     * @reason Route to own function
     */
    @Overwrite(remap = false)
    public boolean canSnowAtBody(int x, int y, int z, boolean checkLight)
    {
        ISeasonState seasonState = SeasonHelper.getSeasonState((World) (Object) this);
        return SeasonASMHelper.canSnowAtInSeason((World) (Object) this, x, y, z, checkLight, seasonState);
    }

    /**
     * @author darkshadow44
     * @reason Route to own function
     */
    @Overwrite(remap = false)
    public boolean canBlockFreezeBody(int x, int y, int z, boolean noWaterAdj)
    {
        ISeasonState seasonState = SeasonHelper.getSeasonState((World) (Object) this);
        return SeasonASMHelper.canBlockFreezeInSeason((World) (Object) this, x, y, z, noWaterAdj, seasonState);
    }

    /**
     * @author darkshadow44
     * @reason Route to own function
     */
    @Overwrite
    public boolean canLightningStrikeAt(int x, int y, int z)
    {
        if (!isRaining())
            return false;
        if (!canBlockSeeTheSky(x, y, z))
            return false;
        if (getPrecipitationHeight(x, z) > y)
            return false;
        ISeasonState seasonState = SeasonHelper.getSeasonState((World) (Object) this);
        return SeasonASMHelper.isRainingAtInSeason((World) (Object) this, x, y, z, seasonState);
    }
}
