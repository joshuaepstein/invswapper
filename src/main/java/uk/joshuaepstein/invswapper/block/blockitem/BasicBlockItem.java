package uk.joshuaepstein.invswapper.block.blockitem;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import uk.joshuaepstein.invswapper.InvSwapMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicBlockItem extends BlockItem {
	private final List<Component> tooltip = new ArrayList<>();

	public BasicBlockItem(Block block) {
		this(block, (new Item.Properties()).tab(InvSwapMod.INV_SWAP_GROUP));
	}

	public BasicBlockItem(Block block, Item.Properties properties) {
		super(block, properties);
	}

	public BasicBlockItem withTooltip(Component tooltip) {
		this.tooltip.add(tooltip);
		return this;
	}

	public BasicBlockItem withTooltip(Component... tooltip) {
		this.tooltip.addAll(Arrays.asList(tooltip));
		return this;
	}

	public BasicBlockItem withTooltip(List<Component> tooltip) {
		this.tooltip.addAll(tooltip);
		return this;
	}

	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		if (!this.tooltip.isEmpty()) {
			tooltip.add(TextComponent.EMPTY);
			tooltip.addAll(this.tooltip);
		}
	}
}
