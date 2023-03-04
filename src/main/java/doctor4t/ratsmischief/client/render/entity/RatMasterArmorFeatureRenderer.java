package doctor4t.ratsmischief.client.render.entity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexConsumer;
import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.item.RatMasterArmorItem;
import doctor4t.ratsmischief.common.item.RatMasterHoodItem;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;

public class RatMasterArmorFeatureRenderer<T extends PlayerEntity, A extends PlayerEntityModel<T>> extends FeatureRenderer<T, A> {
	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();
	private final A leggingsModel;
	private final A bodyModel;
	private final boolean slim;

	public RatMasterArmorFeatureRenderer(FeatureRendererContext<T, A> context, A leggingsModel, A bodyModel, boolean slim) {
		super(context);
		this.leggingsModel = leggingsModel;
		this.bodyModel = bodyModel;
		this.slim = slim;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.CHEST, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.LEGS, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.FEET, i);
		this.renderArmor(matrixStack, vertexConsumerProvider, livingEntity, EquipmentSlot.HEAD, i);
	}

	private void renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, T entity, EquipmentSlot armorSlot, int light) {
		ItemStack itemStack = entity.getEquippedStack(armorSlot);
		if (itemStack.getItem() instanceof RatMasterArmorItem armorItem) {
			if (armorSlot == EquipmentSlot.HEAD && RatMasterHoodItem.isHidden(itemStack)) return;
			A model = this.getModel(armorSlot);
			if (armorItem.getSlotType() == armorSlot) {
				this.getContextModel().setAttributes(model);
				this.setVisible(model, armorSlot);
				this.setPoses(model, armorSlot);
				this.renderArmorParts(matrices, vertexConsumers, light, armorItem, itemStack.hasGlint(), model, this.usesSecondLayer(armorSlot));
			}
		}
	}

	private A getModel(EquipmentSlot slot) {
		return this.usesSecondLayer(slot) ? this.leggingsModel : this.bodyModel;
	}

	protected void setVisible(A bipedModel, EquipmentSlot slot) {
		bipedModel.setVisible(false);
		switch (slot) {
			case HEAD -> {
				bipedModel.head.visible = true;
				bipedModel.hat.visible = true;
			}
			case CHEST -> {
				bipedModel.body.visible = true;
				bipedModel.rightArm.visible = true;
				bipedModel.leftArm.visible = true;
				bipedModel.jacket.visible = true;
				bipedModel.rightSleeve.visible = true;
				bipedModel.leftSleeve.visible = true;
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

	private void setPoses(A model, EquipmentSlot armorSlot) {
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

	private void renderArmorParts(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ArmorItem item, boolean usesSecondLayer, A model, boolean legs) {
		VertexConsumer vertexConsumer = ItemRenderer.getArmorGlintConsumer(vertexConsumers, RenderLayer.getArmorCutoutNoCull(this.getArmorTexture(item, legs)), false, usesSecondLayer);
		model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0F);
	}

	private boolean usesSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	private Identifier getArmorTexture(ArmorItem item, boolean legs) {
		String string = "textures/entity/armor/" + item.getMaterial().getName() + "_layer_" + (legs ? 2 : slim ? "1_slim" : 1) + ".png";
		return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
	}
}
