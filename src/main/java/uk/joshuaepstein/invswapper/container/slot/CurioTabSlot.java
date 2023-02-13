package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.common.inventory.CurioSlot;

public class CurioTabSlot extends CurioSlot {
	public CurioTabSlot(Player player, IDynamicStackHandler handler, int index, String identifier, int xPosition, int yPosition, NonNullList<Boolean> renders) {
		super(player, handler, index, identifier, xPosition, yPosition, renders);
	}
}
