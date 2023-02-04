package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Predicate;

public class FilteredSlot extends SlotItemHandler {
  private final Predicate<ItemStack> stackFilter;
  
  public FilteredSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, Predicate<ItemStack> stackFilter) {
    super(itemHandler, index, xPosition, yPosition);
    this.stackFilter = stackFilter;
  }
  
  public boolean mayPlace(ItemStack stack) {
    if (!this.stackFilter.test(stack))
      return false; 
    return super.mayPlace(stack);
  }
}
