package uk.joshuaepstein.invswapper.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import uk.joshuaepstein.invswapper.InvSwapMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BasicItem extends Item {
	private final List<Component> tooltip = new ArrayList<>();

	public BasicItem(ResourceLocation id) {
		this(id, (new Item.Properties()).tab(InvSwapMod.INV_SWAP_GROUP));
	}

	public BasicItem(ResourceLocation id, Item.Properties properties) {
		super(properties);
		setRegistryName(id);
	}

	public BasicItem withTooltip(Component tooltip) {
		this.tooltip.add(tooltip);
		return this;
	}

	public BasicItem withTooltip(Component... tooltip) {
		this.tooltip.addAll(Arrays.asList(tooltip));
		return this;
	}

	public BasicItem withTooltip(List<Component> tooltip) {
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
