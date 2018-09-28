/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

import net.minecraft.util.IStringSerializable;
import sereneseasons.season.SeasonTime;

public enum Season implements IStringSerializable
{
    SPRING, SUMMER, AUTUMN, WINTER;

    public enum SubSeason implements ISeasonColorProvider, IStringSerializable
    {
        EARLY_SPRING(SPRING, 0x778087, 0.85F, 0x6F818F, 0.85F, 0x869A68),
        MID_SPRING(SPRING, 0x6F818F, 0x5F849F, 0x7EA271),
        LATE_SPRING(SPRING, 0x678297, 0x3F89BF, 0x6EB283),
        EARLY_SUMMER(SUMMER, 0x73808B, 0x5F849F, 0x76AC6C),
        MID_SUMMER(SUMMER, 0xFFFFFF, 0xFFFFFF, 0x80A755),
        LATE_SUMMER(SUMMER, 0x877777, 0x9F5F5F, 0x98A54B),
        EARLY_AUTUMN(AUTUMN, 0x8F6F6F, 0xC44040, 0xB1A442),
        MID_AUTUMN(AUTUMN, 0x9F5F5F, 0xEF2121, 0xE2A231),
        LATE_AUTUMN(AUTUMN, 0xAF4F4F, 0.85F, 0xDB3030, 0.85F, 0xC98A35),
        EARLY_WINTER(WINTER, 0xAF4F4F, 0.60F, 0xDB3030, 0.60F, 0xB1723B),
        MID_WINTER(WINTER, 0xAF4F4F, 0.45F, 0xDB3030, 0.45F, 0xA0824D),
        LATE_WINTER(WINTER, 0x8E8181, 0.60F, 0xA57070, 0.60F, 0x8F925F);
        
        private Season season;
        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;
        private int birchColor;
        
        SubSeason(Season season, int grassColour, float grassSaturation, int foliageColour, float foliageSaturation, int birchColor)
        {
            this.season = season;
            this.grassOverlay = grassColour;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation; 
            this.birchColor = birchColor;
        }
        
        SubSeason(Season season, int grassColour, int foliageColour, int birchColor)
        {
            this(season, grassColour, -1, foliageColour, -1, birchColor);
        }
        
        public ISeasonState getDefaultState()
        {
        	return new SeasonTime(this);
        }
        
        public Season getSeason()
        {
            return this.season;
        }
        
        public int getGrassOverlay()
        {
            return this.grassOverlay;
        }
        
        public float getGrassSaturationMultiplier()
        {
            return this.grassSaturationMultiplier;
        }
        
        public int getFoliageOverlay()
        {
            return this.foliageOverlay;
        }
        
        public float getFoliageSaturationMultiplier()
        {
            return this.foliageSaturationMultiplier;
        }
        
        public int getBirchColor()
        {
            return this.birchColor;
        }

		@Override
		public String getName() {
			return toString().toLowerCase();
		}
    }

    public enum TropicalSeason implements ISeasonColorProvider, IStringSerializable
    {
        EARLY_DRY(0xFFFFFF, 0xFFFFFF, 0x80A755),
        MID_DRY(0xA58668, 0.8F, 0xB7867C, 0.95F, 0x98A54B),
        LATE_DRY(0x8E7B6D, 0.9F, 0xA08B86, 0.975F, 0x80A755),
        EARLY_WET(0x758C8A, 0x728C91, 0x80A755),
        MID_WET(0x548384, 0x2498AE, 0x76AC6C),
        LATE_WET(0x658989, 0x4E8893, 0x80A755);

        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;
        private int birchColor;

        TropicalSeason(int grassColour, float grassSaturation, int foliageColour, float foliageSaturation, int birchColor)
        {
            this.grassOverlay = grassColour;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation;
            this.birchColor = birchColor;
        }

        TropicalSeason(int grassColour, int foliageColour, int birchColor)
        {
            this(grassColour, -1, foliageColour, -1, birchColor);
        }

        public int getGrassOverlay()
        {
            return this.grassOverlay;
        }

        public float getGrassSaturationMultiplier()
        {
            return this.grassSaturationMultiplier;
        }

        public int getFoliageOverlay()
        {
            return this.foliageOverlay;
        }

        public float getFoliageSaturationMultiplier()
        {
            return this.foliageSaturationMultiplier;
        }
        
        public int getBirchColor()
        {
            return this.birchColor;
        }

		@Override
		public String getName() {
			return toString().toLowerCase();
		}
    }

	@Override
	public String getName() {
		return toString().toLowerCase();
	}
}
