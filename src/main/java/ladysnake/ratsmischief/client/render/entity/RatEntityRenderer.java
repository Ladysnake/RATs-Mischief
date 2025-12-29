package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RatEntityRenderer extends GeoEntityRenderer<RatEntity> {
	public RatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RatEntityModel());
		this.shadowRadius = 0.35f;
		this.addRenderLayer(new EnderEyeFeatureRenderer(this, new EnderEyeEntityRenderer(context, new RatEntityModel())));
		this.addRenderLayer(new PartyHatFeatureRenderer(this, new PartyHatEntityRenderer(context, new RatEntityModel())));
	}

	@Override
	public Vec3d getPositionOffset(RatEntity entity, float tickDelta) {
		if (entity.isSneaking()) {
			return new Vec3d(0, 0.15, 0);
		}

		return super.getPositionOffset(entity, tickDelta);
	}

	@Override
	public void render(RatEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (entity.isFlying() && entity.age < 5) {
			return;
		}

		super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(MatrixStack poseStack, RatEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("bodybone") && !(animatable.isSitting() || animatable.isSneaking())) {
			ItemStack itemStack = animatable.getEquippedStack(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				poseStack.push();
				poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
				poseStack.translate(bone.getPosX(), bone.getPosZ(), bone.getPosY() - 0.05);
				poseStack.scale(0.7f, 0.7f, 0.7f);
				poseStack.multiply(new Quaternionf(bone.getRotX(), bone.getRotZ(), bone.getRotY(), 1.0F));

				MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, packedLight, packedOverlay, poseStack, bufferSource, animatable.getWorld(), 0);
				poseStack.pop();

				// restore the render buffer - GeckoLib expects this state otherwise you'll have weird texture issues
				buffer = bufferSource.getBuffer(RenderLayer.getEntityCutout(this.getTexture(animatable)));
			}
		}

		super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void scaleModelForRender(float widthScale, float heightScale, MatrixStack poseStack, RatEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
		super.scaleModelForRender(animatable.isBaby() ? widthScale / 2 : widthScale, animatable.isBaby() ? heightScale / 2 : heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
	}

	@Override
	protected int getBlockLight(RatEntity entity, BlockPos pos) {
		if (entity.getRatType() == RatEntity.Type.RAT_KID && entity.getRatColor() == DyeColor.PURPLE) {
			return 15;
		} else {
			return super.getBlockLight(entity, pos);
		}
	}
}
