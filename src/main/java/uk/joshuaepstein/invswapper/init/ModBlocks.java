package uk.joshuaepstein.invswapper.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegistryEvent;
import uk.joshuaepstein.invswapper.InvSwapMod;
import uk.joshuaepstein.invswapper.block.InvArmorStand;
import uk.joshuaepstein.invswapper.block.blockitem.InvArmorStandBlockItem;
import uk.joshuaepstein.invswapper.block.entity.InvArmorStandBE;
import uk.joshuaepstein.invswapper.block.renderer.InvArmorStandRenderer;

import java.util.function.Consumer;

public class ModBlocks {
	public static final InvArmorStand INV_ARMOR_STAND = new InvArmorStand();
	public static final BlockItem INV_ARMOR_STAND_BLOCKITEM = new InvArmorStandBlockItem(INV_ARMOR_STAND);

	private static void registerBlock(RegistryEvent.Register<Block> event, Block block, ResourceLocation id) {
		block.setRegistryName(id);
		event.getRegistry().register(block);
	}	public static final BlockEntityType<InvArmorStandBE> INV_ARMOR_STAND_BE = BlockEntityType.Builder.of(InvArmorStandBE::new, INV_ARMOR_STAND).build(null);

	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		registerBlock(event, INV_ARMOR_STAND, InvSwapMod.id("inv_armor_stand"));
	}

	public static void registerBlockItems(RegistryEvent.Register<Item> event) {
		registerBlockItem(event, INV_ARMOR_STAND, INV_ARMOR_STAND_BLOCKITEM);
	}

	public static void registerTileEntities(RegistryEvent.Register<BlockEntityType<?>> event) {
		registerTileEntity(event, INV_ARMOR_STAND_BE, InvSwapMod.id("inv_armor_stand_entity"));
	}

	private static void registerTileEntity(RegistryEvent.Register<BlockEntityType<?>> event, BlockEntityType<?> tileEntity, ResourceLocation id) {
		tileEntity.setRegistryName(id);
		event.getRegistry().register(tileEntity);
	}

	public static void registerTileEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(INV_ARMOR_STAND_BE, InvArmorStandRenderer::new);
	}



	private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block) {
		registerBlockItem(event, block, 64);
	}

	private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize) {
		registerBlockItem(event, block, maxStackSize, properties -> {});
	}

	private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, int maxStackSize, Consumer<Item.Properties> adjustProperties) {
		Item.Properties properties = new Item.Properties().tab(InvSwapMod.INV_SWAP_GROUP).stacksTo(maxStackSize);
		adjustProperties.accept(properties);
		registerBlockItem(event, block, new BlockItem(block, properties));
	}

	private static void registerBlockItem(RegistryEvent.Register<Item> event, Block block, BlockItem blockItem) {
		blockItem.setRegistryName(block.getRegistryName());
		event.getRegistry().register(blockItem);
	}

	private static void registerBlockItemWithEffect(RegistryEvent.Register<Item> event, Block
													 block, int maxStackSize, Consumer<Item.Properties> adjustProperties) {
		Item.Properties properties = new Item.Properties().tab(InvSwapMod.INV_SWAP_GROUP).stacksTo(maxStackSize);
		adjustProperties.accept(properties);
		BlockItem blockItem = new BlockItem(block, properties) {
			public boolean isFoil(ItemStack stack) {
				return true;
			}
		};
		registerBlockItem(event, block, blockItem);
	}

	private static void registerTallBlockItem(RegistryEvent.Register<Item> event, Block block) {
		DoubleHighBlockItem tallBlockItem = new DoubleHighBlockItem(block, (new Item.Properties()).tab(InvSwapMod.INV_SWAP_GROUP).stacksTo(64));
		tallBlockItem.setRegistryName(block.getRegistryName());
		event.getRegistry().register(tallBlockItem);
	}




}
