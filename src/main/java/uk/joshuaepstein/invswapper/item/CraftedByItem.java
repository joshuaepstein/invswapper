package uk.joshuaepstein.invswapper.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import uk.joshuaepstein.invswapper.InvSwapMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CraftedByItem extends BasicItem {
	private final List<Component> tooltip = new ArrayList<>();

	public CraftedByItem(ResourceLocation id, int stacksTo) {
		super(id, (new Properties()).tab(InvSwapMod.INV_SWAP_GROUP).stacksTo(stacksTo));
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player) {
		super.onCraftedBy(stack, level, player);
		CompoundTag stackTag = stack.getOrCreateTag();
		stackTag.putString("craftedBy", player.getName().getString());
		stack.setTag(stackTag);
	}

	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if(stack.hasTag() && stack.getTag().contains("craftedBy")) {
			tooltip.add(new TextComponent(ChatFormatting.GOLD + "Crafted by: " + ChatFormatting.GRAY + stack.getTag().getString("craftedBy")));
		}
	}
}
