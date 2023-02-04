package uk.joshuaepstein.invswapper.container.slot.player;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshuaepstein.invswapper.container.slot.ReadOnlySlot;

public class ArmorViewSlot extends ReadOnlySlot {
  private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] { InventoryMenu.EMPTY_ARMOR_SLOT_HELMET, InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS, InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS };
  
  private final EquipmentSlot equipmentSlotType;
  
  public ArmorViewSlot(Inventory inv, EquipmentSlot equipmentSlotType, int xPosition, int yPosition) {
    super(inv, 39 - (equipmentSlotType.getIndex()), xPosition, yPosition);
    this.equipmentSlotType = equipmentSlotType;
  }
  
  @OnlyIn(Dist.CLIENT)
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[this.equipmentSlotType.getIndex()]);
  }
}
