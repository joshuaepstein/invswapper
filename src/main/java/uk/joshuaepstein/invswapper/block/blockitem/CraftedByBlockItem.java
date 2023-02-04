package uk.joshuaepstein.invswapper.block.blockitem;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CraftedByBlockItem extends BasicBlockItem {
	public CraftedByBlockItem(Block block) {
		super(block);
	}

	public CraftedByBlockItem(Block block, Properties properties) {
		super(block, properties);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player) {
		super.onCraftedBy(stack, level, player);
		CompoundTag tag = stack.getOrCreateTag();
		tag.putString("craftedBy", player.getName().getString());
		stack.setTag(tag);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (stack.hasTag() && stack.getTag().contains("craftedBy"))
			tooltip.add(Component.nullToEmpty(ChatFormatting.GOLD + "Crafted by: " + ChatFormatting.GRAY + stack.getTag().getString("craftedBy")));
	}
}
