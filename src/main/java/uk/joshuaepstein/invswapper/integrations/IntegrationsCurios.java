package uk.joshuaepstein.invswapper.integrations;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import top.theillusivec4.curios.api.CuriosApi;
import uk.joshuaepstein.invswapper.client.screens.StatueScreen;

import java.awt.*;

public class IntegrationsCurios {
	public static boolean curiosLoaded = false;

	public static void curiosLoaded() {
		curiosLoaded = true;
	}

	public static void renderCurios(StatueScreen screen, Rectangle bounds, PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		CuriosApi.getSlotHelper().getSlotTypes().forEach(slotType -> {
			CuriosApi.getCuriosHelper().getCuriosHandler(Minecraft.getInstance().player).ifPresent(handler -> {
				System.out.println("Curios: " + handler.getCurios().get(slotType).getSlots());
			});
		});
	}
}
