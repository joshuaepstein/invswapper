package uk.joshuaepstein.invswapper.container.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;

public interface PlayerSensitiveSlot {
  default ItemStack modifyTakenStack(Player player, ItemStack taken, boolean simulate) {
    return modifyTakenStack(player, taken, player.getCommandSenderWorld().isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER, simulate);
  }
  
  ItemStack modifyTakenStack(Player paramPlayerEntity, ItemStack paramItemStack, LogicalSide paramLogicalSide, boolean paramBoolean);
}
