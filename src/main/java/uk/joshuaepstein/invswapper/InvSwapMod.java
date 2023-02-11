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
	public static boolean isCuriosLoaded = false;

	public InvSwapMod() {
		MinecraftForge.EVENT_BUS.register(this);
		isCuriosLoaded = ModList.get().isLoaded("curios");
	}

	public static ResourceLocation id(String name) {
		return new ResourceLocation(MOD_ID, name);
	}

	public static String sId(String name) {
		return MOD_ID + ":" + name;
	}
}
