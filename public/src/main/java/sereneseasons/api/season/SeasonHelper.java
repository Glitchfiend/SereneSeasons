/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.world.level.Level;

public class SeasonHelper 
{
    public static ISeasonDataProvider dataProvider;

    /** 
     * Obtains data about the state of the season cycle in the world. This works both on
     * the client and the server.
     */
    public static ISeasonState getSeasonState(Level world)
    {
        ISeasonState data;

        if (!world.isClientSide())
        {
            data = dataProvider.getServerSeasonState(world);
        }
        else
        {
            data = dataProvider.getClientSeasonState();
        }

        return data;
    }

    public interface ISeasonDataProvider
    {
        ISeasonState getServerSeasonState(Level world);
        ISeasonState getClientSeasonState();
    }
}
