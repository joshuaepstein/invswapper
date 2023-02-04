package uk.joshuaepstein.invswapper.block.blockitem;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import uk.joshuaepstein.invswapper.InvSwapMod;

public class InvArmorStandBlockItem extends BasicBlockItem {
	public InvArmorStandBlockItem(Block block) {
		super(block, new Item.Properties().tab(InvSwapMod.INV_SWAP_GROUP).stacksTo(1));
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player) {
		super.onCraftedBy(stack, level, player);
		CompoundTag blockEntity = stack.getOrCreateTagElement("BlockEntityTag");
		blockEntity.putUUID("Owner", player.getUUID());
		CompoundTag tag = stack.getOrCreateTag();
		tag.put("BlockEntityTag", blockEntity);
		stack.setTag(tag);
	}
}
