package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SellSlot extends Slot {
  public SellSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }
  
  public boolean mayPlace(ItemStack stack) {
    return false;
  }
  
  public void set(ItemStack stack) {
    super.set(stack);
  }
}
