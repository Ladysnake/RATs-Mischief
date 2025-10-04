package ladysnake.ratsmischief.mixin.client;

import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.util.PlayerEntityRenderStateWrapper;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends BipedEntityRenderState> extends EntityModel<T> implements ModelWithArms, ModelWithHead {

	@Shadow
	@Final
	public ModelPart rightArm;
	@Shadow
	@Final
	public ModelPart head;
	@Shadow
	@Final
	public ModelPart leftArm;

	protected BipedEntityModelMixin(ModelPart root) {
		super(root);
	}

	@Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
	private void positionLeftArm(T state, BipedEntityModel.ArmPose armPose, CallbackInfo ci) {
		if (armPose == BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			if (state instanceof PlayerEntityRenderState player && state instanceof PlayerEntityRenderStateWrapper wrapper) {
				if (player.mainArm == Arm.LEFT ?  wrapper.mischief$holdingRatInRightHand() : wrapper.mischief$holdingRatInLeftHand()) {
					this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
					this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
	private void positionRightArm(T state, BipedEntityModel.ArmPose armPose, CallbackInfo ci) {
		if (armPose == BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			if (state instanceof PlayerEntityRenderState player && state instanceof PlayerEntityRenderStateWrapper wrapper) {
				if (player.mainArm == Arm.RIGHT ?  wrapper.mischief$holdingRatInRightHand() : wrapper.mischief$holdingRatInLeftHand()) {
					this.rightArm.yaw = -0.1F + this.head.yaw;
					this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
					ci.cancel();
				}
			}
		}
	}
}
