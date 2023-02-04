package uk.joshuaepstein.invswapper.container.slot.player;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshuaepstein.invswapper.container.slot.ReadOnlySlot;

public class OffHandSlot extends ReadOnlySlot {
  public OffHandSlot(Inventory inv, int xPosition, int yPosition) {
    super(inv, 40, xPosition, yPosition);
  }
  
  @OnlyIn(Dist.CLIENT)
  public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
    return Pair.of(InventoryMenu.BLOCK_ATLAS, InventoryMenu.EMPTY_ARMOR_SLOT_SHIELD);
  }
}
