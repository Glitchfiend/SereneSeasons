/*******************************************************************************
 * Copyright 2016, the Biomes O' Plenty Team
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package sereneseasons.api.season;

public enum Season 
{
    SPRING, SUMMER, AUTUMN, WINTER;

    public enum SubSeason implements ISeasonColorProvider
    {
        EARLY_SPRING(SPRING, 0x778087, 0.85F, 0x6F818F, 0.85F),
        MID_SPRING(SPRING, 0x6F818F, 0x5F849F),
        LATE_SPRING(SPRING, 0x678297, 0x3F89BF),
        EARLY_SUMMER(SUMMER, 0x73808B, 0x5F849F),
        MID_SUMMER(SUMMER, 0xFFFFFF, 0xFFFFFF),
        LATE_SUMMER(SUMMER, 0x877777, 0x9F5F5F),
        EARLY_AUTUMN(AUTUMN, 0x8F6F6F, 0xC44040),
        MID_AUTUMN(AUTUMN, 0x9F5F5F, 0xEF2121),
        LATE_AUTUMN(AUTUMN, 0xAF4F4F, 0.85F, 0xDB3030, 0.85F),
        EARLY_WINTER(WINTER, 0x9F5F5F, 0.60F, 0xA75757, 0.60F),
        MID_WINTER(WINTER, 0x8F6F6F, 0.45F, 0x9F5F5F, 0.45F),
        LATE_WINTER(WINTER, 0xFFFFFF, 0.60F, 0x8F6F6F, 0.60F);
        
        private Season season;
        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;
        
        SubSeason(Season season, int grassColour, float grassSaturation, int foliageColour, float foliageSaturation)
        {
            this.season = season;
            this.grassOverlay = grassColour;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation; 
        }
        
        SubSeason(Season season, int grassColour, int foliageColour)
        {
            this(season, grassColour, -1, foliageColour, -1);
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
    }

    public enum TropicalSeason implements ISeasonColorProvider
    {
        EARLY_DRY(0xFFFFFF, 0xFFFFFF),
        MID_DRY(0xA09683, 0.75F, 0xA08A83, 0.75F),
        LATE_DRY(0x9B968D, 0.9F, 0x988D89, 0.9F),
        EARLY_WET(0x538E89, 0x4E8893),
        MID_WET(0x2AA196, 0x2498AE),
        LATE_WET(0x678582, 0x638085);

        private int grassOverlay;
        private float grassSaturationMultiplier;
        private int foliageOverlay;
        private float foliageSaturationMultiplier;

        TropicalSeason(int grassColour, float grassSaturation, int foliageColour, float foliageSaturation)
        {
            this.grassOverlay = grassColour;
            this.grassSaturationMultiplier = grassSaturation;
            this.foliageOverlay = foliageColour;
            this.foliageSaturationMultiplier = foliageSaturation;
        }

        TropicalSeason(int grassColour, int foliageColour)
        {
            this(grassColour, -1, foliageColour, -1);
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
    }
}
