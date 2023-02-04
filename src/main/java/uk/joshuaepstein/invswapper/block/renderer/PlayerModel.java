package uk.joshuaepstein.invswapper.block.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;

public class PlayerModel extends net.minecraft.client.model.PlayerModel<Player> {
	private final ModelPart rightArmS;
	private final ModelPart leftArmS;
	private final ModelPart leftSleeveS;
	private final ModelPart rightSleeveS;

	public PlayerModel(BlockEntityRendererProvider.Context context) {
		super(context.bakeLayer(ModelLayers.PLAYER), false);
		this.rightArmS = this.rightArm;
		this.leftArmS = this.leftArm;
		this.leftSleeveS = this.leftSleeve;
		this.rightSleeveS = this.rightSleeve;
	}

	protected Iterable<ModelPart> bodyParts() {
		return Iterables.concat(super.bodyParts(), ImmutableList.of(this.rightArmS, this.leftArmS, this.leftSleeveS, this.rightSleeveS));
	}

	public void setSlim(boolean slim, PoseStack stack) {
		ModelPart modelpart = this.getArm(HumanoidArm.LEFT);
		ModelPart modelpart1 = this.getArm(HumanoidArm.RIGHT);
		if (slim) {
			float f = 0.5F * (float) -1;
			modelpart.x += f;
			modelpart.translateAndRotate(stack);
			modelpart.x -= f;
			f = 0.5F;
			modelpart1.x += f;
			modelpart1.translateAndRotate(stack);
			modelpart1.x -= f;
		} else {
			modelpart.translateAndRotate(stack);
			modelpart1.translateAndRotate(stack);
		}
	}

}
