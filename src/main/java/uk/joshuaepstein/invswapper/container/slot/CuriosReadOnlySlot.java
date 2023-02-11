package uk.joshuaepstein.invswapper.container.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.Curios;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;

public class CuriosReadOnlySlot extends Slot {
	ResourceLocation backgroundSlotResource = new ResourceLocation("curios", "textures/gui/empty_armor_slot.png");
	public CuriosReadOnlySlot(ItemStack stack, ISlotType slotType, Player player, int xPosition, int yPosition) {
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
		this.setBackground(InventoryMenu.BLOCK_ATLAS, player.getCommandSenderWorld().isClientSide() ?
				CuriosApi.getIconHelper().getIcon(slotType.getIdentifier())
				: new ResourceLocation(Curios.MODID, "item/empty_curio_slot"));
	}

	public boolean mayPlace(ItemStack stack) {
		return false;
	}

	public boolean mayPickup(Player playerIn) {
		return false;
	}
}
