package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EnderEyeEntityRenderer extends GeoEntityRenderer<RatEntity> {
	protected EnderEyeEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}

	@Override
	public void render(RatEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		this.animatable = entity;

		RenderLayer renderLayer = RenderLayer.getEntityCutout(EnderEyeFeatureRenderer.TEXTURE);
		this.defaultRender(poseStack, entity, bufferSource, renderLayer, bufferSource.getBuffer(renderLayer), entityYaw, partialTick, packedLight);
	}
}
