package doctor4t.ratsmischief.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import doctor4t.ratsmischief.client.RatsMischiefClient;
import doctor4t.ratsmischief.client.render.entity.RatMasterArmorFeatureRenderer;
import doctor4t.ratsmischief.common.init.ModItems;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
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
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
		super(ctx, model, shadowRadius);
	}

	@Inject(method = "getArmPose", at = @At("HEAD"), cancellable = true)
	private static void ratsmischief$holdRat(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		ItemStack stack = player.getStackInHand(hand);
		if (stack.isOf(ModItems.RAT)) {
			cir.setReturnValue(BipedEntityModel.ArmPose.BOW_AND_ARROW);
		}
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mischief$masterArmor(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
		this.addFeature(
				new RatMasterArmorFeatureRenderer<>(this,
						new PlayerEntityModel<>(ctx.getPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false),
						new PlayerEntityModel<>(ctx.getPart(slim ? RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER_SLIM : RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER),
								slim), slim));
	}

	@Inject(method = "setModelPose", at = @At(value = "TAIL"))
	private void ratsmischief$setModelPose(AbstractClientPlayerEntity player, CallbackInfo ci, @Local(ordinal = 0) PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel) {
		if (!player.isSpectator()) {
			boolean mainHandRat = player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.RAT);
			boolean offHandRat = player.getStackInHand(Hand.OFF_HAND).isOf(ModItems.RAT);
			if (player.getMainArm() == Arm.RIGHT) {
				if (mainHandRat) playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
				if (offHandRat) playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			} else {
				if (mainHandRat) playerEntityModel.leftArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
				if (offHandRat) playerEntityModel.rightArmPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
			}
		}
	}
}
