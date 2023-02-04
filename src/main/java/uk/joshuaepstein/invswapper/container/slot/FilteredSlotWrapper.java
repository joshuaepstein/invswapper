package uk.joshuaepstein.invswapper.container.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class FilteredSlotWrapper extends Slot {
  private final Slot decorated;
  
  private final Predicate<ItemStack> canInsert;
  
  public FilteredSlotWrapper(Slot decorated, Predicate<ItemStack> canInsert) {
    super(decorated.container, decorated.getSlotIndex(), decorated.x, decorated.y);
    this.canInsert = canInsert;
    this.index = decorated.index;
    this.decorated = decorated;
  }
  
  public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
    this.decorated.onQuickCraft(oldStackIn, newStackIn);
  }
  
  public void onTake(Player thePlayer, ItemStack stack) {
    this.decorated.onTake(thePlayer, stack);
  }
  
  public boolean mayPlace(ItemStack stack) {
    if (!this.canInsert.test(stack))
      return false; 
    return this.decorated.mayPlace(stack);
  }
  
  public ItemStack getItem() {
    return this.decorated.getItem();
  }
  
  public boolean hasItem() {
    return this.decorated.hasItem();
  }
  
  public void set(ItemStack stack) {
    this.decorated.set(stack);
  }
  
  public void setChanged() {
    this.decorated.setChanged();
  }
  
  public int getMaxStackSize() {
    return this.decorated.getMaxStackSize();
  }
  
  public int getMaxStackSize(ItemStack stack) {
    return this.decorated.getMaxStackSize(stack);
  }
  
  @Nullable
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    return this.decorated.getNoItemIcon();
  }
  
  public ItemStack remove(int amount) {
    return this.decorated.remove(amount);
  }
  
  public boolean mayPickup(Player playerIn) {
    return this.decorated.mayPickup(playerIn);
  }
  
  public boolean isActive() {
    return this.decorated.isActive();
  }
  
  public int getSlotIndex() {
    return this.decorated.getSlotIndex();
  }
  
  public boolean isSameInventory(Slot other) {
    return this.decorated.isSameInventory(other);
  }
  
  public Slot setBackground(ResourceLocation atlas, ResourceLocation sprite) {
    return this.decorated.setBackground(atlas, sprite);
  }
}
