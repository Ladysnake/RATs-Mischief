package doctor4t.ratsmischief.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import doctor4t.ratsmischief.client.model.RatEntityModel;
import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class RatEntityRenderer extends GeoEntityRenderer<RatEntity> {
	// variables needed for later
	private ItemStack itemStack;
	private VertexConsumerProvider vertexConsumerProvider;
	private Identifier ratTexture;

	public RatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RatEntityModel());
		this.shadowRadius = 0.35f;
		this.addLayer(new PartyHatFeatureRenderer(this, new PartyHatEntityRenderer(context, new RatEntityModel())));
		this.addLayer(new EnderEyeFeatureRenderer(this, new EnderEyeEntityRenderer(context, new RatEntityModel())));
	}

	@Override
	public Vec3d getPositionOffset(RatEntity rat, float tickDelta) {
		if (rat.isSneaking()) {
			return new Vec3d(0, 0.15, 0);
		}

		return super.getPositionOffset(rat, tickDelta);
	}

	@Override
	public void render(RatEntity ratEntity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (ratEntity.isFlying() && ratEntity.age < 5) {
			return;
		}

		super.render(ratEntity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderEarly(RatEntity ratEntity, MatrixStack stackIn, float ticks, VertexConsumerProvider vertexConsumerProvider, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
		this.itemStack = ratEntity.isSitting() || ratEntity.isSneaking() ? ItemStack.EMPTY : ratEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.ratTexture = this.getTexture(ratEntity);

		super.renderEarly(ratEntity, stackIn, ticks, vertexConsumerProvider, vertexBuilder, packedLightIn, packedOverlayIn, red,
				green, blue, partialTicks);
	}

	@Override
	public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("bodybone")) {
			stack.push();
			stack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
			stack.translate(bone.getPositionX(), bone.getPositionZ(), bone.getPositionY() - 0.05);
			stack.scale(0.7f, 0.7f, 0.7f);
			stack.multiply(new Quaternion(bone.getRotationX(), bone.getRotationZ(), bone.getRotationY(), false));

			MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, this.vertexConsumerProvider, 0);
			stack.pop();

			// restore the render buffer - GeckoLib expects this state otherwise you'll have weird texture issues
			bufferIn = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(ratTexture));
		}

		super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	protected int getBlockLight(RatEntity rat, BlockPos blockPos) {
		if (rat.getRatType() == RatEntity.Type.RAT_KID && rat.getRatColor() == DyeColor.PURPLE) {
			return 15;
		} else {
			return super.getBlockLight(rat, blockPos);
		}
	}
}
