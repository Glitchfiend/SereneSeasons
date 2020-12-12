/*******************************************************************************
 * Copyright 2014-2020, the Serene Seasons Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.world.GameRules;

import static net.minecraft.world.GameRules.register;
import static sereneseasons.api.SSGameRules.*;

public class ModGameRules
{
    public static void init()
    {
        RULE_DOSEASONTICK = register("doSeasonTick", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    }
}
