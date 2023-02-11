package uk.joshuaepstein.invswapper.container;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import uk.joshuaepstein.invswapper.block.entity.InvArmorStandBE;
import uk.joshuaepstein.invswapper.init.ModContainers;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class StatueContainer extends AbstractContainerMenu {

	private final BlockPos tilePos;
	public Inventory container = new Inventory(null);
	public List<Slot> playerSlots = new ArrayList<>();
	public Inventory playerInventory;
	public CompoundTag curiosSlots = new CompoundTag();

	public StatueContainer(int id, Level level, BlockPos pos, Inventory playerInventory) {
		super(ModContainers.STATUE_CONTAINER, id);
		this.tilePos = pos;
		if (level.getBlockEntity(pos) instanceof InvArmorStandBE invArmorStandBE) {
			container = invArmorStandBE.getInventory();
			curiosSlots = invArmorStandBE.getCuriosSlots();
		} else {
			container = new Inventory(null);
			throw new IllegalStateException("BlockEntity is not an instance of InvArmorStandBE");
		}
		initSlots(container, playerInventory);
		this.playerInventory = playerInventory;
	}

	private void initSlots(Inventory container, Inventory playerInventory) {
		// Player inv slots
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, (84 + row * 18) + 24));
			}
		}
//		 Player hotbar slots
		for (int row = 0; row < 9; ++row) {
			this.addSlot(new Slot(playerInventory, row, 8 + row * 18, 142+24));
		}

	}

	@Nullable
	public InvArmorStandBE getTile(Level world) {
		BlockEntity tile = world.getBlockEntity(tilePos);
		if (tile instanceof InvArmorStandBE invArmorStandBE) return invArmorStandBE;
		return null;
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		return ItemStack.EMPTY;
	}

	@Override
	public boolean stillValid(Player player) {
		BlockEntity tile = player.level.getBlockEntity(tilePos);
		return tile instanceof InvArmorStandBE && ((InvArmorStandBE) tile).getOwner().equals(player.getUUID());
	}

	public BlockPos getTilePos() {
		return this.tilePos;
	}
}
