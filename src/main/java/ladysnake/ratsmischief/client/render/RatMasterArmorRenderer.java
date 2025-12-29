package ladysnake.ratsmischief.client.render;

import com.google.common.collect.Maps;
import ladysnake.ratsmischief.client.RatsMischiefClient;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatMasterCloakItem;
import ladysnake.ratsmischief.common.item.RatMasterHoodItem;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Map;

public class RatMasterArmorRenderer implements ArmorRenderer {
	public static final RatMasterArmorRenderer INSTANCE = new RatMasterArmorRenderer();

	private static final Map<String, Identifier> ARMOR_TEXTURE_CACHE = Maps.newHashMap();

	private boolean slim;
	private PlayerEntityModel<LivingEntity> leggingsModel;
	private PlayerEntityModel<LivingEntity> playerModel;

	@Override
	public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, LivingEntity entity, EquipmentSlot armorSlot, int light, BipedEntityModel<LivingEntity> contextModel) {
		if (entity instanceof AbstractClientPlayerEntity player) {
			if (this.leggingsModel == null || this.playerModel == null) {
				var loader = MinecraftClient.getInstance().getEntityModelLoader();

				this.slim = "slim".equals(player.getModel());
				this.leggingsModel = new PlayerEntityModel<>(loader.getModelPart(RatsMischiefClient.RAT_MASTER_ARMOR_INNER_LAYER), false);
				this.playerModel = new PlayerEntityModel<>(loader.getModelPart(this.slim ? RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER_SLIM : RatsMischiefClient.RAT_MASTER_ARMOR_OUTER_LAYER), this.slim);
			}

			PlayerEntityModel<LivingEntity> armorModel = this.getRatModel(armorSlot);

			contextModel.copyBipedStateTo(armorModel);
			this.setRatVisible(armorModel, armorSlot, stack);
			this.setRatPoses(armorModel, armorSlot);

			ArmorRenderer.renderPart(matrices, vertexConsumers, light, stack, armorModel, this.getRatArmorTexture(this.usesRatSecondLayer(armorSlot)));
		}
	}

	private boolean usesRatSecondLayer(EquipmentSlot slot) {
		return slot == EquipmentSlot.LEGS;
	}

	private PlayerEntityModel<LivingEntity> getRatModel(EquipmentSlot slot) {
		return this.usesRatSecondLayer(slot) ? this.leggingsModel : this.playerModel;
	}

	private void setRatVisible(PlayerEntityModel<LivingEntity> bipedModel, EquipmentSlot slot, ItemStack stack) {
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

	private Identifier getRatArmorTexture(boolean legs) {
		String string = "textures/models/armor/rat_master_layer_" + (legs ? "2" : this.slim ? "1_slim" : "1") + ".png";
		return ARMOR_TEXTURE_CACHE.computeIfAbsent(string, RatsMischief::id);
	}
}
