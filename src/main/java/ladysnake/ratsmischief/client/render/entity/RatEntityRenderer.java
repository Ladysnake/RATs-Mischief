package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RatEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<RatEntity, R> {
	public RatEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new RatEntityModel());
		this.shadowRadius = 0.35f;
		this.addRenderLayer(new EnderEyeFeatureRenderer<>(this, new EnderEyeEntityRenderer<>(context, new RatEntityModel())));
		this.addRenderLayer(new PartyHatFeatureRenderer<>(this, new PartyHatEntityRenderer<>(context, new RatEntityModel())));
	}

	@Override
	public void addRenderData(RatEntity animatable, Void relatedObject, R renderState) {
		renderState.addGeckolibData(RatEntityModel.PARTY_HAT_TEXTURE, animatable.getPartyHat().ordinal());
		renderState.addGeckolibData(RatEntityModel.DYE_COLOR_TICKET, animatable.getRatColor());
		renderState.addGeckolibData(RatEntityModel.TYPE_TICKET, animatable.getRatType());
		renderState.addGeckolibData(RatEntityModel.SNIFFING_TICKET, animatable.isSniffing());
		renderState.addGeckolibData(RatEntityModel.EATING_TICKET, animatable.isEating());
		renderState.addGeckolibData(RatEntityModel.FLYING_TICKET, animatable.isFlying());
		renderState.addGeckolibData(RatEntityModel.AROUSED_TICKET, animatable.isAroused());
		renderState.addGeckolibData(RatEntityModel.SPY_TICKET, animatable.isSpy());
	}

	@Override
	public Vec3d getPositionOffset(R state) {
		if (state.sneaking) {
			return new Vec3d(0, 0.15, 0);
		}

		return super.getPositionOffset(state);
	}

	@Override
	public void render(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		if (renderState.getGeckolibData(RatEntityModel.FLYING_TICKET) && renderState.age < 5) {
			return;
		}

		super.render(renderState, poseStack, bufferSource, packedLight);
	}

	@Override
	public void renderRecursively(R renderState, MatrixStack poseStack, GeoBone bone, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, boolean isReRender, int packedLight, int packedOverlay, int renderColor) {
		if (bone.getName().equals("bodybone") && !(renderState.getGeckolibData(RatEntityModel.SITTING_TICKET) || renderState.sneaking)) {
			ItemStack itemStack = (ItemStack) renderState.getGeckolibData(DataTickets.EQUIPMENT_BY_SLOT).get(EquipmentSlot.MAINHAND);
			if (!itemStack.isEmpty()) {
				poseStack.push();
				poseStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
				poseStack.translate(bone.getPosX(), bone.getPosZ(), bone.getPosY() - 0.05);
				poseStack.scale(0.7f, 0.7f, 0.7f);
				poseStack.multiply(new Quaternionf(bone.getRotX(), bone.getRotZ(), bone.getRotY(), 1.0F));

				MinecraftClient.getInstance().getItemRenderer().renderItem(itemStack, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, packedLight, packedOverlay, poseStack, bufferSource, MinecraftClient.getInstance().world, 0);
				poseStack.pop();

				// restore the render buffer - GeckoLib expects this state otherwise you'll have weird texture issues
				buffer = bufferSource.getBuffer(RenderLayer.getEntityCutout(this.getTextureLocation(renderState)));
			}
		}
		super.renderRecursively(renderState, poseStack, bone, renderType, bufferSource, buffer, isReRender, packedLight, packedOverlay, renderColor);
	}

	@Override
	public void scaleModelForRender(R renderState, float widthScale, float heightScale, MatrixStack poseStack, BakedGeoModel model, boolean isReRender) {
		super.scaleModelForRender(renderState, renderState.baby ? widthScale / 2 : widthScale, renderState.baby ? heightScale / 2 : heightScale, poseStack, model, isReRender);
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
