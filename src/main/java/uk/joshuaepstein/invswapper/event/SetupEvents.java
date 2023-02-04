package uk.joshuaepstein.invswapper.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import uk.joshuaepstein.invswapper.init.ModScreens;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {
	@SubscribeEvent
	public static void setupCommon(FMLCommonSetupEvent event) {
		// Network Register when created.
		ModScreens.register();
	}

	@SubscribeEvent
	public static void setupDedicatedServer(FMLDedicatedServerSetupEvent event) {}
}
