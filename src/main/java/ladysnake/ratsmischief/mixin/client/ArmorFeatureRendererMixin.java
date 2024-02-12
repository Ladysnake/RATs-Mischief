package ladysnake.ratsmischief.mixin.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ladysnake.ratsmischief.client.RatsMischiefClient;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.item.RatMasterCloakItem;
import ladysnake.ratsmischief.common.item.RatMasterHoodItem;
import ladysnake.ratsmischief.common.item.RatMasterMaskItem;
import ladysnake.ratsmischief.common.util.EntityRendererWrapper;
import ladysnake.ratsmischief.common.util.PlayerEntityRendererWrapper;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
	@Unique
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	@Unique
	private static final Map<String, Identifier> SLIM_ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	@Unique
	private PlayerEntityModel<LivingEntity> leggingsModel;
	@Unique
	private PlayerEntityModel<LivingEntity> playerModel;
	@Unique
	private boolean slim = false;

	public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
		super(context);
	}

	@Inject(method = "<init>", at = @At("TAIL"))
	private void mischief$init(FeatureRendererContext<T, M> context, A leggingsModel, A bodyModel, CallbackInfo ci) {
		if (context instanceof EntityRendererWrapper wrapper) {
			if (context instanceof PlayerEntityRendererWrapper playerWrapper) {
				this.slim = playerWrapper.isSlim();
			}
			this.leggingsModel = new PlayerEntityModel<>(wrapper.getContext().getPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false);
			this.playerModel = new PlayerEntityModel<>(wrapper.getContext().getPart(this.slim ? RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER_SLIM : RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER), this.slim);
		}
	}

	@Inject(method = "renderArmor", at = @At("HEAD"), cancellable = true)
	private void mischief$rattyRender(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light, A model, CallbackInfo ci) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof RatMasterArmorItem) {
			this.renderRatArmor(matrices, vertexConsumers, entity, armorSlot, light);
			ci.cancel();
			return;
		}
		if (armorSlot == EquipmentSlot.HEAD && RatMasterMaskItem.isWearingMask(entity)) {
			ci.cancel();
		}
	}

	@Unique
	private void renderRatArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof RatMasterArmorItem armorItem) {
			PlayerEntityModel<LivingEntity> armorModel = this.getRatModel(armorSlot);
			if (armorItem.getSlotType() == armorSlot) {
				{
					armorModel.handSwingProgress = this.getContextModel().handSwingProgress;
					armorModel.riding = this.getContextModel().riding;
					armorModel.child = this.getContextModel().child;
					armorModel.leftArmPose = this.getContextModel().leftArmPose;
					armorModel.rightArmPose = this.getContextModel().rightArmPose;
					armorModel.sneaking = this.getContextModel().sneaking;
					armorModel.head.copyTransform(this.getContextModel().head);
					armorModel.hat.copyTransform(this.getContextModel().hat);
					armorModel.body.copyTransform(this.getContextModel().body);
					armorModel.rightArm.copyTransform(this.getContextModel().rightArm);
					armorModel.leftArm.copyTransform(this.getContextModel().leftArm);
					armorModel.rightLeg.copyTransform(this.getContextModel().rightLeg);
					armorModel.leftLeg.copyTransform(this.getContextModel().leftLeg);
				}
				this.setRatVisible(armorModel, armorSlot, itemStack);
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

				this.renderRatArmorParts(matrices, vertexConsumers, light, armorItem, armorModel, this.usesRatSecondLayer(armorSlot));
			}
		}
	}

	@Unique
	private PlayerEntityModel<LivingEntity> getRatModel(EquipmentSlot slot) {
		return this.usesRatSecondLayer(slot) ? this.leggingsModel : this.playerModel;
	}

	@Unique
	protected void setRatVisible(PlayerEntityModel<LivingEntity> bipedModel, EquipmentSlot slot, ItemStack stack) {
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
	private void setRatPoses(PlayerEntityModel<LivingEntity> model, EquipmentSlot armorSlot) {
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
	private void renderRatArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, PlayerEntityModel<LivingEntity> model, boolean legs) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getRatArmorTexture(item, legs)), false, false);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0F);
	}

	@Unique
	private boolean usesRatSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	@Unique
	private Identifier getRatArmorTexture(ArmorItem item, boolean legs) {
		String string = "textures/entity/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : this.slim ? "1_slim" : 1) + ".png";
		if (this.slim) {
			return SLIM_ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		} else {
			return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		}
	}
}
