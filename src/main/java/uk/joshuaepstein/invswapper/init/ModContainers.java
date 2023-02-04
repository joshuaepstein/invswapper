package uk.joshuaepstein.invswapper.init;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import uk.joshuaepstein.invswapper.container.StatueContainer;

public class ModContainers {
	public static MenuType<StatueContainer> STATUE_CONTAINER;

	public static void register(RegistryEvent.Register<MenuType<?>> event) {
		STATUE_CONTAINER = IForgeMenuType.create((windowId, inv, data) -> {
			return new StatueContainer(windowId, inv.player.level, data.readBlockPos(), inv);
		});
		event.getRegistry().registerAll(STATUE_CONTAINER.setRegistryName("statue_container"));
	}
}
