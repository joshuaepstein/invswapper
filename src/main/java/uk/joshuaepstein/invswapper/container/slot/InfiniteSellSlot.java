package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class InfiniteSellSlot extends SellSlot {
  public InfiniteSellSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
    super(inventoryIn, index, xPosition, yPosition);
  }
  
  public ItemStack remove(int amount) {
    return getItem().copy();
  }
}
