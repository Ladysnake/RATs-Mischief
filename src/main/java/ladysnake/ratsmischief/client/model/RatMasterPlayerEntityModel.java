//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ladysnake.ratsmischief.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import ladysnake.ratsmischief.mixin.accessor.PlayerEntityModelAccessor;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.random.RandomGenerator;

public class RatMasterPlayerEntityModel<T extends LivingEntity> extends PlayerEntityModel<T> {
	public RatMasterPlayerEntityModel(ModelPart root, boolean thinArms) {
		super(root, thinArms);
	}

	public static ModelData getTexturedModelData(Dilation dilation, boolean slim) {
		ModelData modelData = BipedEntityModel.getModelData(dilation, 0.0F);
		ModelPartData modelPartData = modelData.getRoot();
		float d = 0.25F;
		if (slim) {
			modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(5.0F, 2.5F, 0.0F));
			modelPartData.addChild("right_arm", ModelPartBuilder.create().uv(40, 16).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(-5.0F, 2.5F, 0.0F));
			modelPartData.addChild("left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(5.0F, 2.5F, 0.0F));
			modelPartData.addChild("right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(-5.0F, 2.5F, 0.0F));
		} else {
			modelPartData.addChild("left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
			modelPartData.addChild("left_sleeve", ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(5.0F, 2.0F, 0.0F));
			modelPartData.addChild("right_sleeve", ModelPartBuilder.create().uv(40, 32).cuboid(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(-5.0F, 2.0F, 0.0F));
		}

		modelPartData.addChild("left_leg", ModelPartBuilder.create().uv(16, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
		modelPartData.addChild("left_pants", ModelPartBuilder.create().uv(0, 48).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(1.9F, 12.0F, 0.0F));
		modelPartData.addChild("right_pants", ModelPartBuilder.create().uv(0, 32).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.pivot(-1.9F, 12.0F, 0.0F));
		modelPartData.addChild("jacket", ModelPartBuilder.create().uv(16, 32).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, dilation.add(d)), ModelTransform.NONE);

		modelPartData.addChild("ear", ModelPartBuilder.create(), ModelTransform.NONE);
		modelPartData.addChild("cloak", ModelPartBuilder.create(), ModelTransform.NONE);
		modelPartData.addChild("jacket", ModelPartBuilder.create(), ModelTransform.NONE);

		return modelData;
	}

	protected Iterable<ModelPart> getBodyParts() {
		return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
	}

	public void setAngles(T livingEntity, float f, float g, float h, float i, float j) {
		super.setAngles(livingEntity, f, g, h, i, j);
		this.leftPants.copyTransform(this.leftLeg);
		this.rightPants.copyTransform(this.rightLeg);
		this.leftSleeve.copyTransform(this.leftArm);
		this.rightSleeve.copyTransform(this.rightArm);
		this.jacket.copyTransform(this.body);
	}

	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPants.visible = visible;
		this.rightPants.visible = visible;
		this.jacket.visible = visible;
	}

	public void setArmAngle(Arm arm, MatrixStack matrices) {
		ModelPart modelPart = this.getArm(arm);
		if (((PlayerEntityModelAccessor) this).ratsmischief$thinArms()) {
			float f = 0.5F * (float) (arm == Arm.RIGHT ? 1 : -1);
			modelPart.pivotX += f;
			modelPart.rotate(matrices);
			modelPart.pivotX -= f;
		} else {
			modelPart.rotate(matrices);
		}

	}

	public ModelPart getRandomPart(RandomGenerator random) {
		PlayerEntityModelAccessor playerEntityModelAccessor = (PlayerEntityModelAccessor) this;
		return playerEntityModelAccessor.ratsmischief$parts().get(random.nextInt(playerEntityModelAccessor.ratsmischief$parts().size()));
	}

	public enum ArmorPart {
		HOOD,
		CHEST,
		LEGS;
	}
}
