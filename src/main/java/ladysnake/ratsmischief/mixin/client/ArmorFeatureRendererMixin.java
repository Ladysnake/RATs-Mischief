package ladysnake.ratsmischief.mixin.client;

import com.google.common.collect.Maps;
import ladysnake.ratsmischief.client.RatsMischiefClient;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.item.RatMasterCloakItem;
import ladysnake.ratsmischief.common.item.RatMasterHoodItem;
import ladysnake.ratsmischief.common.util.EntityRendererWrapper;
import ladysnake.ratsmischief.common.util.PlayerEntityRendererWrapper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.equipment.EquipmentRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> extends FeatureRenderer<S, M> {
	@Unique
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	@Unique
	private static final Map<String, Identifier> SLIM_ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	@Unique
	private PlayerEntityModel leggingsModel;
	@Unique
	private PlayerEntityModel playerModel;
	@Unique
	private boolean slim = false;

	public ArmorFeatureRendererMixin(FeatureRendererContext<S, M> context) {
		super(context);
	}


	@Inject(method = "<init>(Lnet/minecraft/client/render/entity/feature/FeatureRendererContext;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/client/render/entity/model/BipedEntityModel;Lnet/minecraft/client/render/entity/equipment/EquipmentRenderer;)V", at = @At("TAIL"))
	private void mischief$init(FeatureRendererContext<S, M> context, BipedEntityModel<S> innerModel, BipedEntityModel<S> outerModel, BipedEntityModel<S> babyInnerModel, BipedEntityModel<S> babyOuterModel, EquipmentRenderer equipmentRenderer, CallbackInfo ci) {
		if (context instanceof EntityRendererWrapper wrapper) {
			if (context instanceof PlayerEntityRendererWrapper playerWrapper) {
				this.slim = playerWrapper.mischief$isSlim();
			}
			this.leggingsModel = new PlayerEntityModel(wrapper.mischief$getContext().getPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false);
			this.playerModel = new PlayerEntityModel(wrapper.mischief$getContext().getPart(this.slim ? RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER_SLIM : RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER), this.slim);
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void mischief$rattyRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel, CallbackInfo ci) {
		if (stack.getItem() instanceof RatMasterArmorItem) {
			this.renderRatArmor(matrices, vertexConsumers, stack, slot, light);
			ci.cancel();
		}
	}

	@Unique
	private void renderRatArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot armorSlot, int light) {
		PlayerEntityModel armorModel = this.getRatModel(armorSlot);
		{
			/*armorModel.handSwingProgress = this.getContextModel().handSwingProgress;
			armorModel.riding = this.getContextModel().riding;
			armorModel.child = this.getContextModel().child;
			armorModel.leftArmPose = this.getContextModel().leftArmPose;
			armorModel.rightArmPose = this.getContextModel().rightArmPose;
			armorModel.sneaking = this.getContextModel().sneaking;*/
			armorModel.head.copyTransform(this.getContextModel().head);
			armorModel.hat.copyTransform(this.getContextModel().hat);
			armorModel.body.copyTransform(this.getContextModel().body);
			armorModel.rightArm.copyTransform(this.getContextModel().rightArm);
			armorModel.leftArm.copyTransform(this.getContextModel().leftArm);
			armorModel.rightLeg.copyTransform(this.getContextModel().rightLeg);
			armorModel.leftLeg.copyTransform(this.getContextModel().leftLeg);
		}
		this.setRatVisible(armorModel, armorSlot, stack);
		this.setRatPoses(armorModel, armorSlot);

		if (!playerModel.body.visible) {
			armorModel.body.visible = false;
		}
		if (!playerModel.leftLeg.visible) {
			armorModel.leftLeg.visible = false;
		}
		if (!playerModel.rightLeg.visible) {
			armorModel.rightLeg.visible = false;
		}
		if (!playerModel.head.visible) {
			armorModel.head.visible = false;
		}

		this.renderRatArmorParts(matrices, vertexConsumers, light, armorModel, this.usesRatSecondLayer(armorSlot));

	}

	@Unique
	private PlayerEntityModel getRatModel(EquipmentSlot slot) {
		return this.usesRatSecondLayer(slot) ? this.leggingsModel : this.playerModel;
	}

	@Unique
	protected void setRatVisible(PlayerEntityModel bipedModel, EquipmentSlot slot, ItemStack stack) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD -> {
				bipedModel.head.visible = true;
				if (!RatMasterHoodItem.isHidden(stack)) {
					bipedModel.hat.visible = true;
				}
			}
			case CHEST -> {
				bipedModel.body.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				bipedModel.jacket.visible = true;
				if (!RatMasterCloakItem.isStripped(stack)) {
					bipedModel.rightSleeve.visible = true;
					bipedModel.leftSleeve.visible = true;
				}
			}
			case LEGS -> {
				bipedModel.body.visible = true;
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				bipedModel.jacket.visible = true;
				bipedModel.rightPants.visible = true;
				bipedModel.leftPants.visible = true;
			}
			case FEET -> {
				bipedModel.rightLeg.visible = true;
				bipedModel.leftLeg.visible = true;
				bipedModel.rightPants.visible = true;
				bipedModel.leftPants.visible = true;
			}
		}
	}

	@Unique
	private void setRatPoses(PlayerEntityModel model, EquipmentSlot armorSlot) {
		switch (armorSlot) {
			case CHEST -> {
				model.jacket.copyTransform(model.body);
				model.rightSleeve.copyTransform(model.rightArm);
				model.leftSleeve.copyTransform(model.leftArm);
			}
			case LEGS -> {
				model.jacket.copyTransform(model.body);
				model.rightPants.copyTransform(model.rightLeg);
				model.leftPants.copyTransform(model.leftLeg);
			}
			case FEET -> {
				model.rightPants.copyTransform(model.rightLeg);
				model.leftPants.copyTransform(model.leftLeg);
			}
		}
	}

	@Unique
	private void renderRatArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, PlayerEntityModel model, boolean legs) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getRatArmorTexture(legs)), false);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
	}

	@Unique
	private boolean usesRatSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	@Unique
	private Identifier getRatArmorTexture(boolean legs) {
		String string = "textures/entity/equipment/" + (legs ? "humanoid_leggings" : "humanoid") + "/rat_master" + (slim ? "_slim" : "") + ".png";
		if (this.slim) {
			return SLIM_ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		} else {
			return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		}
	}
}
