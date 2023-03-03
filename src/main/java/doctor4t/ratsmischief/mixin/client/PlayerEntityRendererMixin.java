package doctor4t.ratsmischief.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import doctor4t.ratsmischief.common.init.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
//	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
//    private static void ratsmischief$holdRat(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
//        ItemStack stack = player.getStackInHand(hand);
//        if (stack.isOf(ModItems.RAT)) {
//			cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
//        }
//    }
//
//	@Inject(method = "setModelPose", at = @At(value = "TAIL"))
//	private void ratsmischief$setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci, @Local(ordinal = 0) PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel) {
//		if (!player.isSpectator()) {
//			boolean mainHandRat = player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.RAT);
//			boolean offHandRat = player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.RAT);
//			if (player.getMainArm() == Arm.RIGHT) {
//				if (mainHandRat) playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
//				if (offHandRat) playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
//			} else {
//				if (mainHandRat) playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
//				if (offHandRat) playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
//			}
//		}
//	}
}
