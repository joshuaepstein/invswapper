package uk.joshuaepstein.invswapper.client.screens;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import uk.joshuaepstein.invswapper.InvSwapMod;
import uk.joshuaepstein.invswapper.client.helper.ScreenDrawHelper;
import uk.joshuaepstein.invswapper.container.StatueContainer;
import uk.joshuaepstein.invswapper.container.slot.ReadOnlySlot;
import uk.joshuaepstein.invswapper.container.slot.player.ArmorViewSlot;
import uk.joshuaepstein.invswapper.container.slot.player.OffHandSlot;
import uk.joshuaepstein.invswapper.util.UIHelper;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class StatueScreen extends AbstractContainerScreen<StatueContainer> {
	public static final ResourceLocation TEXTURE = InvSwapMod.id("textures/gui/statue.png");
	protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
	private final List<Slot> slots = new ArrayList();
	private final Inventory containerInventory;
	private final Inventory playerInventory;
	private final int inventorySlotsNum = 27;
	private final int armorSlotsNum = 4;
	private final int hotbarSlotsNum = 9;
	private final ItemStack draggingItem = ItemStack.EMPTY;
	protected Rectangle bounds;
	protected Button selectButton;
	protected boolean isQuickCrafting;
	private boolean isSplittingStack;
	private int snapbackStartX;
	private int snapbackStartY;
	private long snapbackTime;
	private ItemStack snapbackItem = ItemStack.EMPTY;
	@Nullable
	private Slot snapbackEnd;

	public StatueScreen(StatueContainer statueContainer, Inventory inventory, Component component) {
		super(statueContainer, inventory, component);
		this.imageWidth = 18*9+14;
		this.imageHeight = 100;
		this.width = 18*9+30+4+10;
		this.height = 100;
		this.inventoryLabelY = this.imageHeight - 93;
		this.titleLabelX = 8;
		this.containerInventory = statueContainer.container;
		this.playerInventory = statueContainer.playerInventory;
	}

	protected void init() {
		super.init();
		refreshWidgets();
		setBounds(new Rectangle(this.leftPos, this.topPos, this.imageWidth, this.imageHeight));
	}

	public void refreshWidgets() {
		this.slots.clear();
	}

	public void setBounds(Rectangle bounds) {
		this.bounds = bounds;
		this.slots.clear();

		this.slots.add(new ArmorViewSlot(this.containerInventory, EquipmentSlot.FEET, this.getArmorSlotsBoxBounds().x + 7, this.getArmorSlotsBoxBounds().y + 7));
		this.slots.add(new ArmorViewSlot(this.containerInventory, EquipmentSlot.LEGS, this.getArmorSlotsBoxBounds().x + 7, this.getArmorSlotsBoxBounds().y + 7 + (18) + (2)));
		this.slots.add(new ArmorViewSlot(this.containerInventory, EquipmentSlot.CHEST, this.getArmorSlotsBoxBounds().x + 7, this.getArmorSlotsBoxBounds().y + 7 + (18 * 2) + (2 * 2)));
		this.slots.add(new ArmorViewSlot(this.containerInventory, EquipmentSlot.HEAD, this.getArmorSlotsBoxBounds().x + 7, this.getArmorSlotsBoxBounds().y + 7 + (18 * 3) + (2 * 3)));
		this.slots.add(new OffHandSlot(this.containerInventory, this.getMainAndOffhandBoxBounds().x + 7, this.getArmorSlotsBoxBounds().y + getMainAndOffhandBoxBounds().y + 7));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				this.slots.add(new ReadOnlySlot(this.containerInventory, j + i * 9 + 9, 8 + j * 18, 17 + i * 18)); // Slot width: 18, Slot height: 18
			}
		}
		for (int i = 0; i < 9; i++) {
			this.slots.add(new ReadOnlySlot(this.containerInventory, i, 8 + i * 18, 17 + 58)); // Slot width: 18, Slot height: 18
		}
	}

	public Rectangle getArmorSlotsBoxBounds() {
		int armorSlotsWidth = 30;
		return new Rectangle(this.bounds.width+5, 0, armorSlotsWidth, 30*3);
	}

	public Rectangle getMainAndOffhandBoxBounds() {
		int mainAndOffhandWidth = 30;
		return new Rectangle(-mainAndOffhandWidth-5, 0, mainAndOffhandWidth, 30);
	}

	@Override
	public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		this.fillGradient(stack, 0, 0, this.width, this.height, -1072689136, -804253680);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent(this, stack));
		this.renderBg(stack, partialTicks, mouseX, mouseY);
		net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.ContainerScreenEvent.DrawBackground(this, stack, mouseX, mouseY));

		stack.pushPose();
		stack.translate(this.bounds.x, this.bounds.y, 0.0D);
		this.renderContainers(stack);
		this.renderArmorSlots(stack, mouseX, mouseY, partialTicks);
//		this.renderContainerBox(stack, mouseX, mouseY, partialTicks);
//		this.renderPlayer(stack, mouseX, mouseY, partialTicks);
		this.renderText(stack);
		stack.popPose();
		for(Widget widget : this.renderables) {
			widget.render(stack, mouseX, mouseY, partialTicks);
		}
		this.renderTooltip(stack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(PoseStack stack, float p_97788_, int p_97789_, int p_97790_) {
		renderBackground(stack);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.bounds == null) return false;
		double containerX = mouseX - this.bounds.x;
		double containerY = mouseY - this.bounds.y;
		if (this.selectButton != null) return this.selectButton.mouseClicked(containerX, containerY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private void renderContainers(PoseStack stack) {
		Rectangle paddingBounds = new Rectangle(this.getPlayerBoxBounds());
		paddingBounds.x -= 5;
		paddingBounds.width += 10;
		paddingBounds.height += 10;
//		UIHelper.drawContainerBordersGUIStyle(this, stack, paddingBounds);
		UIHelper.drawContainerBordersGUIStyle(this, stack, this.getArmorSlotsBoxBounds());
		UIHelper.drawContainerBordersGUIStyle(this, stack, this.getMainAndOffhandBoxBounds());
	}

	private void renderArmorSlots(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		int slotHover = -2130706433;
		for (Slot slot : this.slots) {
			if (slot == null) continue;
//			if (!(slot instanceof ArmorViewSlot || slot instanceof OffHandSlot)) continue;
			stack.pushPose();
			RenderSystem.applyModelViewMatrix();
			stack.translate(slot.x, slot.y, 0.0D);
			Minecraft.getInstance().getTextureManager().bindForSetup(UIHelper.UI_RESOURCE);
			blit(stack, -1, -1, 173, 0, 18, 18);
			stack.popPose();
		}
		for (Slot slot : this.slots) {
//			if (!(slot instanceof ArmorViewSlot || slot instanceof OffHandSlot)) continue;
			if (slot == null) continue;
			Rectangle box = this.getSlotBox(slot);
			ItemStack slotStack = slot.getItem();
			stack.pushPose();
			stack.translate(slot.x, slot.y, 0.0D);
			this.setBlitOffset(100);
			itemRenderer.blitOffset = 100.0F;
			stack.popPose();
			if (!slotStack.isEmpty()) {
				RenderSystem.getModelViewStack().pushPose();
				this.itemRenderer.renderAndDecorateItem(slotStack, this.bounds.x + slot.x, this.bounds.y + slot.y);
				this.itemRenderer.renderGuiItemDecorations(this.font, slotStack, this.bounds.x + slot.x, this.bounds.y + slot.y, null);
				RenderSystem.getModelViewStack().popPose();
			} else {
				stack.pushPose();
				stack.translate(slot.x, slot.y, 0.0D);
				Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
				if (pair != null) {
					TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
					Minecraft.getInstance().getTextureManager().bindForSetup(textureatlassprite.atlas().location());
					blit(stack, 0, 0, this.getBlitOffset(), 16, 16, textureatlassprite);
				}
				stack.popPose();
			}
			if (box.contains(mouseX - this.bounds.x, mouseY - this.bounds.y)) {
				int slotX = slot.x;
				int slotY = slot.y;
				stack.pushPose();
				stack.translate(slotX, slotY, 0.0D);
				RenderSystem.disableDepthTest();
				RenderSystem.colorMask(true, true, true, false);
				this.fillGradient(stack, 0, 0, 16, 16, slotHover, slotHover);
				RenderSystem.colorMask(true, true, true, true);
				RenderSystem.enableDepthTest();

				stack.popPose();
				if (slot.hasItem()) {
					this.renderTooltip(stack, slotStack, mouseX - this.bounds.x, mouseY - this.bounds.y);
				}
			}
		}
	}

	public void renderText(PoseStack stack) {
		Font font = this.minecraft.font;
		font.draw(stack, this.title, this.titleLabelX, this.titleLabelY, 4210752);
//		font.draw(stack, "Inventory", this.inventoryLabelX, this.inventoryLabelY, 4210752);
	}

	public void renderBackground(PoseStack stack) {
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, UIHelper.UI_RESOURCE);
		fill(stack, this.bounds.x + 5, this.bounds.y + 5, this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, -3750202);

		blit(stack, bounds.x, bounds.y, 0, 44, 5, 5);
		blit(stack, this.bounds.x + this.bounds.width - 5, this.bounds.y, 8, 44, 5, 5);
		blit(stack, this.bounds.x, this.bounds.y + this.bounds.height - 5, 0, 52, 5, 5);
		blit(stack, this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, 8, 52, 5, 5);

		stack.pushPose();
		stack.translate((this.bounds.x + 5), this.bounds.y, 0.0D);
		stack.scale((this.bounds.width - 10), 1.0F, 1.0F);
		blit(stack, 0, 0, 6, 44, 1, 5);
		stack.translate(0.0D, this.bounds.getHeight() - 5.0D, 0.0D);
		blit(stack, 0, 0, 6, 52, 1, 5);
		stack.popPose();

		stack.pushPose();
		stack.translate(bounds.x, bounds.y + 5, 0);
		stack.scale(1, bounds.height - 10, 1);
		blit(stack, 0, 0, 0, 50, 5, 1);
		stack.translate(bounds.getWidth() - 5, 0, 0);
		blit(stack, 0, 0, 8, 50, 5, 1);
		stack.popPose();
	}

	public Rectangle getPlayerBoxBounds() {
		int playerBoxWidth = 80;
		return new Rectangle(-playerBoxWidth-10, 0, playerBoxWidth, 108);
	}

	private Rectangle getSlotBox(Slot slot) {
		return new Rectangle(slot.x - 1, slot.y - 1, 18, 18);
	}

	private void renderContainerBox(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		ScreenDrawHelper.draw(7, DefaultVertexFormat.POSITION_COLOR_TEX, buf -> {
			ScreenDrawHelper.rect(buf, stack, 3.0F, 3.0F).texVanilla(166.0F, 20.0F, 3.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, (this.width - 6), 3.0F).at(3.0F, 0.0F).texVanilla(169.0F, 20.0F, 1.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, 3.0F, 3.0F).at((this.width - 3), 0.0F).texVanilla(170.0F, 20.0F, 3.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, 3.0F, (this.height - 6)).at(0.0F, 3.0F).texVanilla(166.0F, 23.0F, 3.0F, 1.0F).draw();
			ScreenDrawHelper.rect(buf, stack, 3.0F, (this.height - 6)).at((this.width - 3), 3.0F).texVanilla(170.0F, 23.0F, 3.0F, 1.0F).draw();
			ScreenDrawHelper.rect(buf, stack, 3.0F, 3.0F).at(0.0F, (this.height - 3)).texVanilla(166.0F, 24.0F, 3.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, (this.width - 6), 3.0F).at(3.0F, (this.height - 3)).texVanilla(169.0F, 24.0F, 1.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, 3.0F, 3.0F).at((this.width - 3), (this.height - 3)).texVanilla(170.0F, 24.0F, 3.0F, 3.0F).draw();
			ScreenDrawHelper.rect(buf, stack, (this.width - 6), (this.height - 6)).at(3.0F, 3.0F).texVanilla(169.0F, 23.0F, 1.0F, 1.0F).draw();
		});
	}

	private void renderPlayer(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
		Rectangle plBounds = this.getPlayerBoxBounds();
		int offsetX = plBounds.x + plBounds.width / 2;
		int offsetY = plBounds.y + plBounds.height - 4;
		stack.pushPose();
		stack.translate(offsetX, offsetY, 0.0D);
		stack.scale(1.6F, 1.6F, 1.6F);
		UIHelper.drawFacingPlayer(stack, -mouseX + this.bounds.x + offsetX, -mouseY + this.bounds.y + offsetY);
		stack.popPose();
	}

	private void drawSlot(PoseStack matrixStack, Slot slot) {
		ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
		ItemStack slotStack = slot.getItem();
		int slotX = slot.x;
		int slotY = slot.y;
		matrixStack.pushPose();
		matrixStack.translate(slotX, slotY, 0.0D);
		Minecraft.getInstance().getTextureManager().bindForSetup(UIHelper.UI_RESOURCE);
		blit(matrixStack, -1, -1, 173, 0, 18, 18);
		this.setBlitOffset(100);
		itemRenderer.blitOffset = 100.0F;
		if (slotStack.isEmpty()) {
			Pair<ResourceLocation, ResourceLocation> pair = slot.getNoItemIcon();
			if (pair != null) {
				TextureAtlasSprite textureatlassprite = Minecraft.getInstance().getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
				RenderSystem.setShaderTexture(0, textureatlassprite.atlas().location());
				blit(matrixStack, 0, 0, this.getBlitOffset(), 16, 16, textureatlassprite);
			}
		} else {
			RenderSystem.getModelViewStack().pushPose();
			RenderSystem.getModelViewStack().mulPoseMatrix(matrixStack.last().pose());
			RenderSystem.enableDepthTest();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			RenderSystem.enableDepthTest();
			this.itemRenderer.renderAndDecorateItem(this.minecraft.player, slotStack, 0, 0, 0);
			this.itemRenderer.renderGuiItemDecorations(this.font, slotStack, 0, 0, slot.getItem().getCount() == 1 ? "" : slot.getItem().getCount() + "");
			RenderSystem.getModelViewStack().popPose();
		}

		itemRenderer.blitOffset = 0.0F;
		this.setBlitOffset(0);
		matrixStack.popPose();
	}
}
