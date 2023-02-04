package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReadOnlySlot extends Slot {
	public ReadOnlySlot(Container inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	public boolean mayPickup(Player playerIn) {
		return false;
	}
}
