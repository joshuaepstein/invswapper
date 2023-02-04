package uk.joshuaepstein.invswapper.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientSetupEvents {
	@SubscribeEvent(priority = EventPriority.LOW)
	public static void setupClient(FMLClientSetupEvent event) {
		// ModScreens.register()
		// ModKeybinds.register()
		// ModEntityRenderers.register()
		MinecraftForge.EVENT_BUS.register(InputEvents.class);
		// ModTooltips.register(event)
	}
}
