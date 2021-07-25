/*******************************************************************************
 * Copyright 2021, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.init;

import net.minecraft.world.level.GameRules;

import static net.minecraft.world.level.GameRules.register;
import static sereneseasons.api.SSGameRules.*;

public class ModGameRules
{
    public static void init()
    {
        RULE_DOSEASONCYCLE = register("doSeasonCycle", GameRules.Category.UPDATES, GameRules.BooleanValue.create(true));
    }
}
