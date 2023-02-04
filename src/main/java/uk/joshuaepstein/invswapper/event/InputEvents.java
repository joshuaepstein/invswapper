package uk.joshuaepstein.invswapper.event;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class InputEvents {
	private static final Set<InputConstants.Key> KEY_DOWN_SET = new HashSet<>();
	private static boolean isShiftDown;

	public static boolean isShiftDown() {
		return isShiftDown;
	}

	@SubscribeEvent
	public static void onShiftKeyDown(InputEvent.KeyInputEvent event) {
		if (event.getKey() == 340) {
			if (event.getAction() == 1) {
				isShiftDown = true;
			} else if (event.getAction() == 0) {
				isShiftDown = false;
			}
		}
	}

	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null) return;
		onInput(minecraft, InputConstants.getKey(event.getKey(), event.getScanCode()), event.getAction());
	}

	private static void onInput(Minecraft minecraft, InputConstants.Key key, int action) {
		if (action == 1) {
			KEY_DOWN_SET.add(key);
		} else if (action == 0) {
			KEY_DOWN_SET.remove(key);
		}

	}

	@SubscribeEvent
	public static void onMouse(InputEvent.MouseInputEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.level == null)
			return;
		onInput(minecraft, InputConstants.Type.MOUSE.getOrCreate(event.getButton()), event.getAction());
	}

	private static boolean isKeyDown(InputConstants.Key key) {
		return KEY_DOWN_SET.contains(key);
	}

	@SubscribeEvent
	public static void onMouseScroll(InputEvent.MouseScrollEvent event) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.screen == null)
			return;
		double scrollDelta = event.getScrollDelta();
	}
}
