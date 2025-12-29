package ladysnake.ratsmischief.mixin.client;

import ladysnake.ratsmischief.common.init.ModItems;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
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
public class BipedEntityModelMixin<T extends LivingEntity> {
	@Shadow
	public BipedEntityModel.ArmPose leftArmPose;
	@Shadow
	public BipedEntityModel.ArmPose rightArmPose;
	@Shadow
	@Final
	public ModelPart rightArm;
	@Shadow
	@Final
	public ModelPart head;
	@Shadow
	@Final
	public ModelPart leftArm;

	@Inject(method = "positionLeftArm", at = @At("HEAD"), cancellable = true)
	private void positionLeftArm(T entity, CallbackInfo ci) {
		if (this.leftArmPose == BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			if (entity instanceof PlayerEntity player) {
				if (player.getMainArm() == Arm.LEFT ? player.getMainHandStack().isOf(ModItems.RAT) : player.getOffHandStack().isOf(ModItems.RAT)) {
					this.leftArm.yaw = 0.1F + this.head.yaw + 0.4F;
					this.leftArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
					ci.cancel();
				}
			}
		}
	}

	@Inject(method = "positionRightArm", at = @At("HEAD"), cancellable = true)
	private void positionRightArm(T entity, CallbackInfo ci) {
		if (this.rightArmPose == BipedEntityModel.ArmPose.BOW_AND_ARROW) {
			if (entity instanceof PlayerEntity player) {
				if (player.getMainArm() == Arm.RIGHT ? player.getMainHandStack().isOf(ModItems.RAT) : player.getOffHandStack().isOf(ModItems.RAT)) {
					this.rightArm.yaw = -0.1F + this.head.yaw;
					this.rightArm.pitch = (float) (-Math.PI / 2) + this.head.pitch;
					ci.cancel();
				}
			}
		}
	}
}
