package uk.joshuaepstein.invswapper;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import uk.joshuaepstein.invswapper.init.ModBlocks;
import uk.joshuaepstein.invswapper.integrations.IntegrationsCurios;

@Mod(InvSwapMod.MOD_ID)
public class InvSwapMod {

	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "invswapper";
	public static final CreativeModeTab INV_SWAP_GROUP = new CreativeModeTab(MOD_ID) {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ModBlocks.INV_ARMOR_STAND);
		}
	};

	public InvSwapMod() {
		MinecraftForge.EVENT_BUS.register(this);
		if(ModList.get().isLoaded("curios"))
			IntegrationsCurios.curiosLoaded();
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	public static String sId(String name) {
		return MOD_ID + ":" + name;
	}
}
