package ladysnake.ratsmischief.client.render.armor;

import com.google.common.collect.Maps;
import ladysnake.ratsmischief.client.RatsMischiefClient;
import ladysnake.ratsmischief.client.model.RatMasterPlayerEntityModel;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatArmorItem;
import ladysnake.ratsmischief.common.item.RatCloakItem;
import ladysnake.ratsmischief.common.item.RatHoodItem;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Map;

public class RatMasterArmorRenderer implements ArmorRenderer {
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	private static final Map<String, Identifier> SLIM_ARMOR_TEXTURE_CACHE = Maps.newHashMap();

	private RatMasterPlayerEntityModel<LivingEntity> headModel;
	private RatMasterPlayerEntityModel<LivingEntity> chestModel;
	private RatMasterPlayerEntityModel<LivingEntity> chestSlimModel;
	private RatMasterPlayerEntityModel<LivingEntity> legsModel;

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot slot, int light, BipedEntityModel<LivingEntity> contextModel) {
		if (this.headModel == null)
			this.headModel = new RatMasterPlayerEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false);
		if (this.chestModel == null)
			this.chestModel = new RatMasterPlayerEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER), false);
		if (this.chestSlimModel == null)
			this.chestSlimModel = new RatMasterPlayerEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER_SLIM), true);
		if (this.legsModel == null)
			this.legsModel = new RatMasterPlayerEntityModel<>(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false);

		ItemStack itemStack = entity.getEquippedStack(slot);
		if (itemStack.getItem() instanceof RatArmorItem) {
			this.renderRatArmor(matrices, vertexConsumers, entity, slot, light, stack, contextModel);
		}
	}

	public static void setVisible(RatMasterPlayerEntityModel<LivingEntity> bipedModel, BipedEntityModel<?> playerModel) {
		if (!playerModel.body.visible) {
			bipedModel.body.visible = false;
			bipedModel.jacket.visible = false;
		}
		if (!playerModel.leftLeg.visible) {
			bipedModel.leftLeg.visible = false;
			bipedModel.leftPants.visible = false;
		}
		if (!playerModel.rightLeg.visible) {
			bipedModel.rightLeg.visible = false;
			bipedModel.rightPants.visible = false;
		}
		if (!playerModel.head.visible) {
			bipedModel.head.visible = false;
			bipedModel.hat.visible = false;
		}
	}

	private void renderRatArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity, EquipmentSlot armorSlot, int light, ItemStack stack, BipedEntityModel<LivingEntity> contextModel) {
		boolean slim = entity instanceof AbstractClientPlayerEntity player && player.getModel().equals("slim");

		if (stack.getItem() instanceof RatArmorItem armorItem) {
			RatMasterPlayerEntityModel<LivingEntity> armorModel = this.getRatModel(armorSlot, slim);
			if (armorItem.getSlotType() == armorSlot) {
				armorModel.handSwingProgress = contextModel.handSwingProgress;
				armorModel.riding = contextModel.riding;
				armorModel.child = contextModel.child;
				armorModel.leftArmPose = contextModel.leftArmPose;
				armorModel.rightArmPose = contextModel.rightArmPose;
				armorModel.sneaking = contextModel.sneaking;
				contextModel.setAttributes(armorModel);
				armorModel.leftSleeve.copyTransform(contextModel.leftArm);
				armorModel.rightSleeve.copyTransform(contextModel.rightArm);

				this.setRatVisible(armorModel, armorSlot, stack);
				this.setRatPoses(armorModel, armorSlot);
				setVisible(armorModel, contextModel); // for vigorem compat
				this.renderRatArmorParts(matrices, vertexConsumers, light, stack, armorItem, armorModel, this.getArmorPart(armorSlot), RatHoodItem.isDown(stack), slim);
			}
		}
	}

	private RatMasterPlayerEntityModel<LivingEntity> getRatModel(EquipmentSlot slot, boolean slim) {
		return switch (getArmorPart(slot)) {
			case HOOD -> headModel;
			case CHEST -> slim ? chestSlimModel : chestModel;
			case LEGS -> legsModel;
		};
	}

	protected void setRatVisible(RatMasterPlayerEntityModel<LivingEntity> bipedModel, EquipmentSlot slot, ItemStack stack) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD -> {
				bipedModel.hat.visible = true;
				bipedModel.head.visible = true;
			}
			case CHEST -> {
				bipedModel.body.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				bipedModel.jacket.visible = true;
				if (!RatCloakItem.isStripped(stack)) {
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

	private void setRatPoses(RatMasterPlayerEntityModel<LivingEntity> model, EquipmentSlot armorSlot) {
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

	private void renderRatArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, ArmorItem item, RatMasterPlayerEntityModel<LivingEntity> model, RatMasterPlayerEntityModel.ArmorPart armorPart, boolean hoodDown, boolean slim) {
//		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getRatArmorTexture(item, armorPart, hoodDown)), false, false);
//		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0F);

		ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, model, this.getRatArmorTexture(item, armorPart, hoodDown, slim));
	}

	private RatMasterPlayerEntityModel.ArmorPart getArmorPart(EquipmentSlot slot) {
		return switch (slot) {
			case LEGS -> RatMasterPlayerEntityModel.ArmorPart.LEGS;
			case HEAD -> RatMasterPlayerEntityModel.ArmorPart.HOOD;
			default -> RatMasterPlayerEntityModel.ArmorPart.CHEST;
		};
	}

	private Identifier getRatArmorTexture(ArmorItem item, RatMasterPlayerEntityModel.ArmorPart armorPart, boolean hoodDown, boolean slim) {
		String string = "textures/entity/armor/" + item.getMaterial().getName() + "_" + armorPart.name().toLowerCase(Locale.ROOT);
		if (armorPart == RatMasterPlayerEntityModel.ArmorPart.CHEST && slim) {
			string += "_slim";
		}
		if (armorPart == RatMasterPlayerEntityModel.ArmorPart.HOOD && hoodDown) {
			string += "_down";
		}
		string += ".png";

		if (slim) {
			return SLIM_ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		} else {
			return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
		}
	}
}
