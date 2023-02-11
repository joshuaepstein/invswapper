package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReadonlyItemStackSlot extends Slot {
	public ReadonlyItemStackSlot(ItemStack stack, int xPosition, int yPosition) {
		super(new Container() {
			@Override
			public int getContainerSize() {
				return 1;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public ItemStack getItem(int index) {
				return stack;
			}

			@Override
			public ItemStack removeItem(int index, int count) {
				return ItemStack.EMPTY;
			}

			@Override
			public ItemStack removeItemNoUpdate(int index) {
				return ItemStack.EMPTY;
			}

			@Override
			public void setItem(int index, ItemStack stack) {
			}

			@Override
			public void setChanged() {
			}

			@Override
			public boolean stillValid(Player player) {
				return false;
			}

			@Override
			public void clearContent() {
			}
		}, 0, xPosition, yPosition);
	}

	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	public boolean mayPickup(Player playerIn) {
		return false;
	}
}
