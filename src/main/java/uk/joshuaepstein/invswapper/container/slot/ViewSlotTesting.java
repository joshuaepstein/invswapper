package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.entity.player.Inventory;

public class ViewSlotTesting extends ReadOnlySlot {
  public ViewSlotTesting(Inventory inv, int index, int xPosition, int yPosition) {
    super(inv, index, xPosition, yPosition);
  }
}
