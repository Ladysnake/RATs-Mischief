package ladysnake.ratsmischief.client.render.entity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class EnderEyeFeatureRenderer extends GeoRenderLayer<RatEntity> {
	public static final Identifier TEXTURE = new Identifier(RatsMischief.MOD_ID, "textures/entity/ender_eye.png");

	private final EnderEyeEntityRenderer enderEyeEntityRenderer;

	public EnderEyeFeatureRenderer(GeoEntityRenderer<RatEntity> entityRendererIn, EnderEyeEntityRenderer enderEyeEntityRenderer) {
		super(entityRendererIn);
		this.enderEyeEntityRenderer = enderEyeEntityRenderer;
	}

	@Override
	public void render(MatrixStack matrixStackIn, RatEntity ratEntity, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSourceIn, VertexConsumer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (ratEntity.isSpy() && !ratEntity.isInvisible()) {
			this.enderEyeEntityRenderer.defaultRender(
				matrixStackIn,
				ratEntity,
				bufferSourceIn,
				RenderLayer.getEntityCutout(TEXTURE),
				bufferIn,
				0.0F,
				partialTicks,
				packedLightIn
			);
		}
	}
}
