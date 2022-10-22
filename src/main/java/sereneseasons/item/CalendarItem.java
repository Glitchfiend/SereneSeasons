package sereneseasons.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import sereneseasons.api.season.SeasonHelper;
import sereneseasons.config.ServerConfig;
import sereneseasons.season.SeasonTime;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class CalendarItem extends Item
{
    public CalendarItem(Properties p_41383_)
    {
        super(p_41383_);
    }

    /*@Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_42873_, boolean p_42874_)
    {
        if (!world.isClientSide)
        {
            if (stack.isFramed())
            {
                int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
                SeasonTime time = new SeasonTime(seasonCycleTicks);
                int subSeasonDuration = ServerConfig.subSeasonDuration.get();

                stack.setHoverName(Component.translatable("desc.sereneseasons." + time.getSubSeason().toString().toLowerCase(Locale.ROOT)).append(" - ").append(Component.translatable("desc.sereneseasons.day_counter",(time.getDay() % subSeasonDuration) + 1, subSeasonDuration)));
            }
            else
            {
                stack.resetHoverName();
            }
        }
    }*/

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag)
    {
        if (world != null)
        {
            if (ServerConfig.isDimensionWhitelisted(world.dimension()))
            {
                int seasonCycleTicks = SeasonHelper.getSeasonState(world).getSeasonCycleTicks();
                SeasonTime time = new SeasonTime(seasonCycleTicks);
                int subSeasonDuration = ServerConfig.subSeasonDuration.get();

                tooltip.add(Component.translatable("desc.sereneseasons." + time.getSubSeason().toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
                tooltip.add(Component.translatable("desc.sereneseasons.day_counter", (time.getDay() % subSeasonDuration) + 1, subSeasonDuration).withStyle(ChatFormatting.GRAY));
            }
            else
            {
                tooltip.add(Component.literal("???").withStyle(ChatFormatting.GRAY));
            }
        }
        else
        {
            tooltip.add(Component.literal("???").withStyle(ChatFormatting.GRAY));
        }
    }
}
