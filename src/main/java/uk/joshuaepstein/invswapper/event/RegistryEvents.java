package uk.joshuaepstein.invswapper.event;

import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import uk.joshuaepstein.invswapper.init.ModBlocks;
import uk.joshuaepstein.invswapper.init.ModContainers;
import uk.joshuaepstein.invswapper.init.ModItems;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
	@SubscribeEvent
	public static void onBlockRegister(RegistryEvent.Register<Block> event) {
		ModBlocks.registerBlocks(event);
	}

	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		ModItems.registerItems(event);
		ModBlocks.registerBlockItems(event);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		ModBlocks.registerTileEntityRenderers(event);
	}

	@SubscribeEvent
	public static void onTileEntityRegister(RegistryEvent.Register<BlockEntityType<?>> event) {
		ModBlocks.registerTileEntities(event);
	}

	@SubscribeEvent
	public static void onContainerRegister(RegistryEvent.Register<MenuType<?>> event) {
		ModContainers.register(event);
	}
}
