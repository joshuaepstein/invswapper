package uk.joshuaepstein.invswapper.init;

import net.minecraft.world.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import uk.joshuaepstein.invswapper.InvSwapMod;
import uk.joshuaepstein.invswapper.item.CraftedByItem;

public class ModItems {
	public static CraftedByItem SIZE_KEY = new CraftedByItem(InvSwapMod.id("size_key"), 1);

	public static void registerItems(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();
		registry.register(SIZE_KEY);
	}
}
