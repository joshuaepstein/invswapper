package uk.joshuaepstein.invswapper.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshuaepstein.invswapper.block.InvArmorStand;
import uk.joshuaepstein.invswapper.block.entity.InvArmorStandBE;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlockEvents {
	@SubscribeEvent
	public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
		Block block = event.getPlacedBlock().getBlock();
		if (block instanceof InvArmorStand) {
			InvArmorStandBE be = (InvArmorStandBE) event.getWorld().getBlockEntity(event.getPos());
			if (!Objects.equals(be.getOwner().toString(), event.getEntity().getUUID().toString())) {
				event.setCanceled(true);
				if (event.getEntity() instanceof Player) {
					((Player) event.getEntity()).displayClientMessage(new TranslatableComponent("message.invswapper.inv_armor_stand.not_owner").withStyle(ChatFormatting.RED), true);
				}
			}
		}
	}
}
