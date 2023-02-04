package uk.joshuaepstein.invswapper.container.slot.player;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class ArmorEditSlot extends Slot {
  private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] { InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET };

  private final EquipmentSlot slotType;
  
  public ArmorEditSlot(Container inventory, EquipmentSlot slotType, int index, int xPosition, int yPosition) {
    super(inventory, index, xPosition, yPosition);
    this.slotType = slotType;
  }
  
  public boolean mayPlace(ItemStack stack) {
    try {
      return stack.canEquip(this.slotType, null);
    } catch (Exception exc) {
      return (Mob.getEquipmentSlotForItem(stack) == this.slotType);
    }
  }
  
  public int getMaxStackSize() {
    return 1;
  }
  
  @Nullable
  @OnlyIn(Dist.CLIENT)
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    if (this.slotType.getType() != EquipmentSlot.Type.ARMOR)
      return null; 
    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[this.slotType.getIndex()]);
  }
}
