/*******************************************************************************
 * Copyright 2024, the Glitchfiend Team.
 * All rights reserved.
 ******************************************************************************/
package sereneseasons.client.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import sereneseasons.init.ModConfig;
import sereneseasons.init.ModTags;
import sereneseasons.item.CalendarType;

public class ContextCalendarType implements SelectItemModelProperty<CalendarType>
{
    public static final SelectItemModelProperty.Type<ContextCalendarType, CalendarType> TYPE = SelectItemModelProperty.Type.create(
            MapCodec.unit(new ContextCalendarType()), CalendarType.CODEC
    );

    @Nullable
    @Override
    public CalendarType get(ItemStack stack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity holder, int i, ItemDisplayContext itemDisplayContext)
    {
        if (holder == null) return CalendarType.STANDARD;

        Level level = clientLevel;

        if (level == null && holder != null)
        {
            level = holder.level();
        }

        if (level == null || !ModConfig.seasons.isDimensionWhitelisted(level.dimension())) return CalendarType.NONE;

        if (holder != null)
        {
            Holder<Biome> biome = level.getBiome(holder.blockPosition());
            if (biome.is(ModTags.Biomes.TROPICAL_BIOMES)) return CalendarType.TROPICAL;
        }

        return CalendarType.STANDARD;
    }

    @Override
    public Codec<CalendarType> valueCodec()
    {
        return CalendarType.CODEC;
    }


    @Override
    public Type<? extends SelectItemModelProperty<CalendarType>, CalendarType> type()
    {
        return TYPE;
    }
}
