package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EnderEyeFeatureRenderer extends GeoRenderLayer<RatEntity> {
	public static final Identifier TEXTURE = RatsMischief.id("textures/entity/ender_eye.png");

	private final EnderEyeEntityRenderer enderEyeEntityRenderer;

	public EnderEyeFeatureRenderer(GeoEntityRenderer<RatEntity> entityRendererIn, EnderEyeEntityRenderer enderEyeEntityRenderer) {
		super(entityRendererIn);
		this.enderEyeEntityRenderer = enderEyeEntityRenderer;
	}

	@Override
	public void render(MatrixStack poseStack, RatEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlayIn) {
		if (animatable.isSpy() && !animatable.isInvisible()) {
			this.enderEyeEntityRenderer.render(
				animatable,
				animatable.getYaw(),
				partialTick,
				poseStack,
				bufferSource,
				packedLight
			);
		}
	}
}
