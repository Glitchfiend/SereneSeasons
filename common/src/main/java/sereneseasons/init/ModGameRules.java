/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.world.level.GameRules;
import sereneseasons.api.SSGameRules;

import static net.minecraft.world.level.GameRules.register;

public class ModGameRules
{
    public static void init()
    {
        SSGameRules.RULE_DOSEASONCYCLE = register("doSeasonCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    }
}
