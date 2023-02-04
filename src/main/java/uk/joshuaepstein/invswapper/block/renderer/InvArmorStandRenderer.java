package uk.joshuaepstein.invswapper.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import io.netty.util.internal.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ForgeHooksClient;
import uk.joshuaepstein.invswapper.InvSwapMod;
import uk.joshuaepstein.invswapper.block.InvArmorStand;
import uk.joshuaepstein.invswapper.block.entity.InvArmorStandBE;
import uk.joshuaepstein.invswapper.util.SkinProfile;


public class InvArmorStandRenderer implements BlockEntityRenderer<InvArmorStandBE> {

	private static final ResourceLocation STONE_SKIN = InvSwapMod.id("textures/entity/stoneskin.png");
	protected static PlayerModel PLAYER_MODEL;
	private final Minecraft mc = Minecraft.getInstance();
	private final ItemRenderer itemRenderer = mc.getItemRenderer();
	private final BlockRenderDispatcher blockRenderer = mc.getBlockRenderer();

	public InvArmorStandRenderer(BlockEntityRendererProvider.Context context) {
		PLAYER_MODEL = new PlayerModel(context);
	}

	@Override
	public void render(InvArmorStandBE tileentity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		BlockState blockstate = tileentity.getBlockState();
		Direction direction = blockstate.getValue(InvArmorStand.FACING);
		renderBlockState(tileentity.getStand(), poseStack, buffer, this.blockRenderer, tileentity.getLevel(), tileentity.getBlockPos());
		drawPlayerModel(poseStack, buffer, tileentity, combinedLight, combinedOverlay, partialTicks);
	}

	private static void renderBlockState(BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos) {
		try {
			for (RenderType type : RenderType.chunkBufferLayers()) {
				if (ItemBlockRenderTypes.canRenderInLayer(state, type))
					renderBlockState(state, matrixStack, buffer, blockRenderer, world, pos, type);
			}
		} catch (Exception exception) {
			InvSwapMod.LOGGER.error("Error rendering blockstate {} at {} in world {}", state, pos, world, exception);
		}
	}

	private void drawPlayerModel(PoseStack matrixStack, MultiBufferSource buffer, InvArmorStandBE tileEntity, int combinedLight, int combinedOverlay, float partialTicks) {
		BlockState blockState = tileEntity.getBlockState();
		Direction direction = blockState.getValue(InvArmorStand.FACING);
		SkinProfile skin = tileEntity.getSkin();
		ResourceLocation skinLocation = StringUtil.isNullOrEmpty(skin.getLatestNickname()) ? STONE_SKIN : skin.getLocationSkin();
		RenderType renderType = PLAYER_MODEL.renderType(skinLocation);
		PLAYER_MODEL.young = true;
		PLAYER_MODEL.setAllVisible(true);
		PLAYER_MODEL.leftArm.xRot = -120.0F;
		PLAYER_MODEL.leftSleeve.xRot = -120.0F;
		PLAYER_MODEL.rightArm.xRot = -120.0F;
		PLAYER_MODEL.rightSleeve.xRot = -120.0F;
		PLAYER_MODEL.head.zRot = 0.0F;
		PLAYER_MODEL.hat.zRot = 0.0F;

		VertexConsumer vertexBuilder = buffer.getBuffer(renderType);
		float scale = 1.00F;
		float statueOffset = 0.5F;
		matrixStack.pushPose();
		matrixStack.translate(0.5, 0.5D, statueOffset);
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		switch (direction) {
			case NORTH -> matrixStack.mulPose(Vector3f.YP.rotationDegrees(0.0F));
			case SOUTH -> matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
			case WEST -> matrixStack.mulPose(Vector3f.YP.rotationDegrees(270.0F));
			case EAST -> matrixStack.mulPose(Vector3f.YP.rotationDegrees(90.0F));
		}
		matrixStack.scale(scale, scale, scale);
		matrixStack.translate(0.0D, -1.5D, 0.0D);
		PLAYER_MODEL.renderToBuffer(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		matrixStack.popPose();
		if (buffer instanceof MultiBufferSource.BufferSource)
			((MultiBufferSource.BufferSource) buffer).endBatch(renderType);
	}

	public static void renderBlockState(BlockState state, PoseStack matrixStack, MultiBufferSource buffer, BlockRenderDispatcher blockRenderer, Level world, BlockPos pos, RenderType type) {
		ForgeHooksClient.setRenderType(type);
		blockRenderer.getModelRenderer().tesselateBlock(world, blockRenderer.getBlockModel(state), state, pos, matrixStack, buffer.getBuffer(type), false, world.random, 0L, OverlayTexture.NO_OVERLAY);
		ForgeHooksClient.setRenderType(null);
	}
}

