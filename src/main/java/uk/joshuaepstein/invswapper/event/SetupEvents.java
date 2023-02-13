package uk.joshuaepstein.invswapper.event;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.common.CuriosHelper;
import uk.joshuaepstein.invswapper.init.ModScreens;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SetupEvents {
	@SubscribeEvent
	public static void setupCommon(FMLCommonSetupEvent event) {
		// Network Register when created.
	}

	@SubscribeEvent
	public static void setupDedicatedServer(FMLDedicatedServerSetupEvent event) {
		CuriosApi.setCuriosHelper(new CuriosHelper());
	}
}
