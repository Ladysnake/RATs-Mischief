package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EnderEyeFeatureRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<RatEntity, Void, R> {
	public static final Identifier TEXTURE = RatsMischief.id("textures/entity/ender_eye.png");

	private final EnderEyeEntityRenderer<R> enderEyeEntityRenderer;

	public EnderEyeFeatureRenderer(GeoEntityRenderer<RatEntity, R> entityRendererIn, EnderEyeEntityRenderer<R> enderEyeEntityRenderer) {
		super(entityRendererIn);
		this.enderEyeEntityRenderer = enderEyeEntityRenderer;
	}

	@Override
	public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		if (renderState.getGeckolibData(RatEntityModel.SPY_TICKET) && !renderState.invisible) {
			this.enderEyeEntityRenderer.render(renderState, poseStack, bufferSource, packedLight);
		}
	}
}
