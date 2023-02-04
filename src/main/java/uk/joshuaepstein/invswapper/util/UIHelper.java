package uk.joshuaepstein.invswapper.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import uk.joshuaepstein.invswapper.InvSwapMod;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class UIHelper {
	public static final ResourceLocation UI_RESOURCE = InvSwapMod.id("textures/gui/main_gui.png");

	private static final int[] LINE_BREAK_VALUES = new int[]{0, 10, -10, 25, -25};

	public static void renderOverflowHidden(PoseStack matrixStack, Consumer<PoseStack> backgroundRenderer, Consumer<PoseStack> innerRenderer) {
		matrixStack.pushPose();
		RenderSystem.enableDepthTest();
		matrixStack.translate(0.0D, 0.0D, 950.0D);
		RenderSystem.colorMask(false, false, false, false);
		GuiComponent.fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
		RenderSystem.colorMask(true, true, true, true);
		matrixStack.translate(0.0D, 0.0D, -950.0D);
		RenderSystem.depthFunc(518);
		backgroundRenderer.accept(matrixStack);
		RenderSystem.depthFunc(515);
		innerRenderer.accept(matrixStack);
		RenderSystem.depthFunc(518);
		matrixStack.translate(0.0D, 0.0D, -950.0D);
		RenderSystem.colorMask(false, false, false, false);
		GuiComponent.fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
		RenderSystem.colorMask(true, true, true, true);
		matrixStack.translate(0.0D, 0.0D, 950.0D);
		RenderSystem.depthFunc(515);
		RenderSystem.disableDepthTest();
		matrixStack.popPose();
	}

	public static void drawFacingPlayer(PoseStack renderStack, int containerMouseX, int containerMouseY) {
		Player player = Minecraft.getInstance().player;
		if (player != null) {
			drawFacingEntity(player, renderStack, containerMouseX, containerMouseY);
		}
	}

	public static void drawFacingEntity(LivingEntity entity, PoseStack renderStack, int containerMouseX, int containerMouseY) {
		float xYaw = (float)Math.atan((containerMouseX / 40.0F));
		float yPitch = (float)Math.atan((containerMouseY / 40.0F));
		PoseStack modelViewStack = RenderSystem.getModelViewStack();
		modelViewStack.pushPose();
		modelViewStack.translate(0.0D, 0.0D, 350.0D);
		modelViewStack.scale(1.0F, 1.0F, -1.0F);
		RenderSystem.applyModelViewMatrix();
		renderStack.pushPose();
		renderStack.scale(30.0F, 30.0F, 30.0F);
		Quaternion rotationZ = Vector3f.ZP.rotationDegrees(180.0F);
		Quaternion rotationX = Vector3f.XP.rotationDegrees(yPitch * 20.0F);
		rotationZ.mul(rotationX);
		renderStack.mulPose(rotationZ);
		float yBodyRot = entity.yBodyRot;
		float yRot = entity.getYRot();
		float xRot = entity.getXRot();
		float yHeadRotO = entity.yHeadRotO;
		float yHeadRot = entity.yHeadRot;
		entity.yBodyRot = 180.0F + xYaw * 20.0F;
		entity.setYRot(180.0F + xYaw * 40.0F);
		entity.setXRot(-yPitch * 20.0F);
		entity.yHeadRot = entity.getYRot();
		entity.yHeadRotO = entity.getYRot();
		RenderSystem.setShaderLights(Util.make(new Vector3f(0.2F, -1.0F, -1.0F), Vector3f::normalize), Util.make(new Vector3f(0.0F, -0.5F, 1.0F), Vector3f::normalize));
		EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
		rotationX.conj();
		entityRenderDispatcher.overrideCameraOrientation(rotationX);
		entityRenderDispatcher.setRenderShadow(false);
		MultiBufferSource.BufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
		RenderSystem.runAsFancy(() -> entityRenderDispatcher.render((Entity)entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, renderStack, multiBufferSource, 15728880));
		multiBufferSource.endBatch();
		entityRenderDispatcher.setRenderShadow(true);
		entity.yBodyRot = yBodyRot;
		entity.setYRot(yRot);
		entity.setXRot(xRot);
		entity.yHeadRotO = yHeadRotO;
		entity.yHeadRot = yHeadRot;
		renderStack.popPose();
		modelViewStack.popPose();
		RenderSystem.applyModelViewMatrix();
	}

	public static void renderContainerBorder(GuiComponent gui, PoseStack matrixStack, Rectangle screenBounds, int u, int v, int lw, int rw, int th, int bh, int contentColor) {
		int width = screenBounds.width;
		int height = screenBounds.height;
		renderContainerBorder(gui, matrixStack, screenBounds.x, screenBounds.y, width, height, u, v, lw, rw, th, bh, contentColor);
	}

	public static void renderContainerBorder(GuiComponent gui, PoseStack matrixStack, int x, int y, int width, int height, int u, int v, int lw, int rw, int th, int bh, int contentColor) {
		int horizontalGap = width - lw - rw;
		int verticalGap = height - th - bh;
		if (contentColor != 0) {
			GuiComponent.fill(matrixStack, x + lw, y + th, x + lw + horizontalGap, y + th + verticalGap, contentColor);
		}

		gui.blit(matrixStack, x, y, u, v, lw, th);
		gui.blit(matrixStack, x + lw + horizontalGap, y, u + lw + 3, v, rw, th);
		gui.blit(matrixStack, x, y + th + verticalGap, u, v + th + 3, lw, bh);
		gui.blit(matrixStack, x + lw + horizontalGap, y + th + verticalGap, u + lw + 3, v + th + 3, rw, bh);
		matrixStack.pushPose();
		matrixStack.translate(x + lw, y, 0.0D);
		matrixStack.scale((float)horizontalGap, 1.0F, 1.0F);
		gui.blit(matrixStack, 0, 0, u + lw + 1, v, 1, th);
		matrixStack.translate(0.0D, th + verticalGap, 0.0D);
		gui.blit(matrixStack, 0, 0, u + lw + 1, v + th + 3, 1, bh);
		matrixStack.popPose();
		matrixStack.pushPose();
		matrixStack.translate(x, y + th, 0.0D);
		matrixStack.scale(1.0F, (float)verticalGap, 1.0F);
		gui.blit(matrixStack, 0, 0, u, v + th + 1, lw, 1);
		matrixStack.translate(lw + horizontalGap, 0.0D, 0.0D);
		gui.blit(matrixStack, 0, 0, u + lw + 3, v + th + 1, rw, 1);
		matrixStack.popPose();
	}

	public static void renderLabelAtRight(GuiComponent gui, PoseStack matrixStack, String text, int x, int y) {
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(0.0F, 0.5F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, UI_RESOURCE);
		Font fontRenderer = minecraft.font;
		int textWidth = fontRenderer.width(text);
		matrixStack.pushPose();
		matrixStack.translate(x, y, 0.0D);
		float scale = 0.75F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(-9.0D, 0.0D, 0.0D);
		gui.blit(matrixStack, 0, 0, 143, 36, 9, 24);
		int gap = 5;
		int remainingWidth = textWidth + 2 * gap;
		matrixStack.translate(-remainingWidth, 0.0D, 0.0D);
		while (remainingWidth > 0) {
			gui.blit(matrixStack, 0, 0, 136, 36, 6, 24);
			remainingWidth -= 5;
			matrixStack.translate(Math.min(5, remainingWidth), 0.0D, 0.0D);
		}
		matrixStack.translate((-textWidth - 2 * gap - 6), 0.0D, 0.0D);
		gui.blit(matrixStack, 0, 0, 121, 36, 14, 24);
		fontRenderer.draw(matrixStack, text, (14 + gap), 9.0F, 14476028);
		matrixStack.popPose();
	}

	public static void renderLabelAtLeft(GuiComponent gui, PoseStack matrixStack, String text, int x, int y) {
		Minecraft minecraft = Minecraft.getInstance();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(0.0F, 0.5F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, UI_RESOURCE);
		Font fontRenderer = minecraft.font;
		int textWidth = fontRenderer.width(text);
		matrixStack.pushPose();
		matrixStack.translate(x, y, 0.0D);
		float scale = -0.75F;
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(-7.5D, -5.0D, 0.0D);
		gui.blit(matrixStack, 0, 0, 143, 36, 9, 24);
		int gap = 5;
		int remainingWidth = textWidth + 2 * gap;
		matrixStack.translate(-remainingWidth, 0.0D, 0.0D);
		while (remainingWidth > 0) {
			gui.blit(matrixStack, 0, 0, 136, 36, 6, 24);
			remainingWidth -= 5;
			matrixStack.translate(Math.min(5, remainingWidth), 0.0D, 0.0D);
		}
		matrixStack.translate((-textWidth - 2 * gap - 6), 0.0D, 0.0D);
		gui.blit(matrixStack, 0, 0, 121, 36, 14, 24);
		matrixStack.scale(-1.0F, -1.0F, -1.0F);
		matrixStack.translate(-textWidth - 2 * gap - 14 - 15, -24.0D, 0.0D);
		fontRenderer.draw(matrixStack, text, (14 + gap), 9.0F, 14476028);
		matrixStack.popPose();
	}

	public static int renderCenteredWrappedText(PoseStack matrixStack, net.minecraft.network.chat.Component text, int maxWidth, int padding) {
		Minecraft minecraft = Minecraft.getInstance();
		net.minecraft.client.gui.Font fontRenderer = minecraft.font;
		java.util.List<FormattedText> lines = getLines(ComponentUtils.mergeStyles(text.copy(), text.getStyle()), maxWidth - 3 * padding);
		int length = lines.stream().mapToInt(fontRenderer::width).max().orElse(0);
		java.util.List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(lines);
		matrixStack.pushPose();
		matrixStack.translate(((float)(-length) / 2.0F), 0.0D, 0.0D);

		for(int i = 0; i < processors.size(); ++i) {
			fontRenderer.draw(matrixStack, processors.get(i), (float)padding, (float)(10 * i + padding), -15130590);
		}

		matrixStack.popPose();
		return processors.size();
	}

	private static java.util.List<FormattedText> getLines(Component component, int maxWidth) {
		Minecraft minecraft = Minecraft.getInstance();
		StringSplitter charactermanager = minecraft.font.getSplitter();
		java.util.List<FormattedText> list = null;
		float f = 3.4028235E38F;
		int[] var6 = LINE_BREAK_VALUES;
		int var7 = var6.length;

		for(int var8 = 0; var8 < var7; ++var8) {
			int i = var6[var8];
			java.util.List<FormattedText> list1 = charactermanager.splitLines(component, maxWidth - i, Style.EMPTY);
			float f1 = Math.abs(getTextWidth(charactermanager, list1) - (float)maxWidth);
			if (f1 <= 10.0F) {
				return list1;
			}

			if (f1 < f) {
				f = f1;
				list = list1;
			}
		}

		return list;
	}

	private static float getTextWidth(StringSplitter manager, List<FormattedText> text) {
		return (float) text.stream().mapToDouble(manager::stringWidth).max().orElse(0.0D);
	}

	public static int renderWrappedText(PoseStack matrixStack, net.minecraft.network.chat.Component text, int maxWidth, int padding) {
		return renderWrappedText(matrixStack, text, maxWidth, padding, -15130590);
	}

	public static int renderWrappedText(PoseStack matrixStack, net.minecraft.network.chat.Component text, int maxWidth, int padding, int color) {
		Minecraft minecraft = Minecraft.getInstance();
		Font fontRenderer = minecraft.font;
		java.util.List<FormattedText> lines = getLines(ComponentUtils.mergeStyles(text.copy(), text.getStyle()), maxWidth - 3 * padding);
		java.util.List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(lines);

		for(int i = 0; i < processors.size(); ++i) {
			fontRenderer.draw(matrixStack, processors.get(i), (float)padding, (float)(10 * i + padding), color);
		}

		return processors.size();
	}

	public static String formatTimeString(int remainingTicks) {
		long seconds = remainingTicks / 20 % 60;
		long minutes = remainingTicks / 20 / 60 % 60;
		long hours = remainingTicks / 20 / 60 / 60;
		return hours > 0L ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
	}
	
	public static <T extends Screen> void drawContainerBordersGUIStyle(T screen, PoseStack stack, Rectangle bounds) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, UIHelper.UI_RESOURCE);
		GuiComponent.fill(stack, bounds.x + 5, bounds.y + 5, bounds.x + bounds.width - 5, bounds.y + bounds.height - 5, -3750202);

		screen.blit(stack, bounds.x, bounds.y, 0, 44, 5, 5);
		screen.blit(stack, bounds.x + bounds.width - 5, bounds.y, 8, 44, 5, 5);
		screen.blit(stack, bounds.x, bounds.y + bounds.height - 5, 0, 52, 5, 5);
		screen.blit(stack, bounds.x + bounds.width - 5, bounds.y + bounds.height - 5, 8, 52, 5, 5);

		stack.pushPose();
		stack.translate((bounds.x + 5), bounds.y, 0.0D);
		stack.scale((bounds.width - 10), 1.0F, 1.0F);
		screen.blit(stack, 0, 0, 6, 44, 1, 5);
		stack.translate(0.0D, bounds.getHeight() - 5.0D, 0.0D);
		screen.blit(stack, 0, 0, 6, 52, 1, 5);
		stack.popPose();

		stack.pushPose();
		stack.translate(bounds.x, bounds.y + 5, 0);
		stack.scale(1, bounds.height - 10, 1);
		screen.blit(stack, 0, 0, 0, 50, 5, 1);
		stack.translate(bounds.getWidth() - 5, 0, 0);
		screen.blit(stack, 0, 0, 8, 50, 5, 1);
		stack.popPose();
	}

	public static <T extends Screen> void drawSlot(T screen, PoseStack matrixStack, Slot slot) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		ItemStack slotStack = slot.getItem();
		int slotX = slot.x;
		int slotY = slot.y;
		matrixStack.pushPose();
		matrixStack.translate(slotX, slotY, 0.0D);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, UI_RESOURCE);
		screen.blit(matrixStack, -1, -1, 173, 0, 18, 18);
		screen.setBlitOffset(100);
		itemRenderer.blitOffset = 100.0F;
		if (slotStack.isEmpty()) {
			Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
			if (pair != null) {
				TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
				Minecraft.getInstance().getTextureManager().bindForSetup(textureatlassprite.atlas().location());
				GuiComponent.blit(matrixStack, 0, 0, screen.getBlitOffset(), 16, 16, textureatlassprite);
			}
		} else {
			RenderSystem.getModelViewStack().pushPose();
			RenderSystem.getModelViewStack().mulPoseMatrix(matrixStack.last().pose());
			RenderSystem.enableDepthTest();
			itemRenderer.renderAndDecorateItem(Minecraft.getInstance().player, slotStack, 0, 0, 0);
			itemRenderer.renderGuiItemDecorations(Minecraft.getInstance().font, slotStack, 0, 0, null);
			RenderSystem.getModelViewStack().popPose();
		}

		itemRenderer.blitOffset = 0.0F;
		screen.setBlitOffset(0);
		matrixStack.popPose();
	}

	private Rectangle getSlotBox(Slot slot) {
		return new Rectangle(slot.x - 1, slot.y - 1, 18, 18);
	}
}
