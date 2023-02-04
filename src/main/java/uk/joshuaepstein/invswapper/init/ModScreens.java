package uk.joshuaepstein.invswapper.init;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import uk.joshuaepstein.invswapper.client.screens.StatueScreen;

@OnlyIn(Dist.CLIENT)
public class ModScreens {
	public static void register() {
		MenuScreens.register(ModContainers.STATUE_CONTAINER, StatueScreen::new);
	}
}
