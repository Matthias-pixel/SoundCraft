package de.ideaonic703.soundcraft.item.custom;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class CDItem extends Item {
    public CDItem(Settings settings) {
        super(settings);
    }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if(stack.hasNbt()) {
            stack.setNbt(new NbtCompound());
        }
        return super.use(world, user, hand);
    }
    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(stack.hasNbt() && stack.getNbt() != null) {
            NbtList songs = stack.getNbt().getList("songs", NbtElement.STRING_TYPE);
            for(NbtElement song : songs) {
                tooltip.add(Text.literal(song.asString()));
            }
        }
    }

}
