package ladysnake.ratsmischief.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RatEntityRenderer extends GeoEntityRenderer<RatEntity> {
	// variables needed for later
	private ItemStack itemStack;
	private VertexConsumerProvider vertexConsumerProvider;
	private Identifier ratTexture;

	public RatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RatEntityModel());
		this.shadowRadius = 0.35f;
		this.addRenderLayer(new PartyHatFeatureRenderer(this, new PartyHatEntityRenderer(context, new RatEntityModel())));
		this.addRenderLayer(new EnderEyeFeatureRenderer(this, new EnderEyeEntityRenderer(context, new RatEntityModel())));
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
	public void preRender(MatrixStack poseStack, RatEntity ratEntity, BakedGeoModel model, VertexConsumerProvider vertexConsumerProvider, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		this.itemStack = ratEntity.isSitting() || ratEntity.isSneaking() ? ItemStack.EMPTY : ratEntity.getEquippedStack(EquipmentSlot.MAINHAND);
		this.vertexConsumerProvider = vertexConsumerProvider;
		this.ratTexture = this.getTexture(ratEntity);

		super.preRender(poseStack, ratEntity, model, vertexConsumerProvider, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void renderRecursively(MatrixStack stack, RatEntity animatable, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSourceIn, VertexConsumer bufferIn, boolean isReRender, float partialTick, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
		if (bone.getName().equals("bodybone")) {
			stack.push();
			stack.multiply(Axis.X_POSITIVE.rotationDegrees(-90));
			stack.translate(bone.getPosX(), bone.getPosZ(), bone.getPosY() - 0.05);
			stack.scale(0.7f, 0.7f, 0.7f);
			stack.multiply(new Quaternionf(bone.getRotX(), bone.getRotZ(), bone.getRotY(), 1.0F));

			MinecraftClient.getInstance().getItemRenderer().renderItem(this.itemStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, packedLightIn, packedOverlayIn, stack, this.vertexConsumerProvider, animatable.getWorld(), 0);
			stack.pop();

			// restore the render buffer - GeckoLib expects this state otherwise you'll have weird texture issues
			bufferIn = this.vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.ratTexture));
		}

		super.renderRecursively(stack, animatable, bone, renderType, bufferSourceIn, bufferIn, isReRender, partialTick, packedLightIn, packedOverlayIn, red, green, blue, alpha);
	}

	@Override
	protected int getBlockLight(RatEntity rat, BlockPos blockPos) {
		if (rat.getRatType() == RatEntity.Type.RAT_KID && rat.getRatColor() == DyeColor.PURPLE) {
			return 15;
		} else {
			return super.getBlockLight(rat, blockPos);
		}
	}

	@Override
	public boolean hasLabel(RatEntity animatable) {
		return super.hasLabel(animatable) && !animatable.isInvisible();
	}
}
