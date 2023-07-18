package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class EnderEyeFeatureRenderer extends GeoLayerRenderer<RatEntity> {
	public static final Identifier TEXTURE = new Identifier(RatsMischief.MOD_ID, "textures/entity/ender_eye.png");

	private final EnderEyeEntityRenderer enderEyeEntityRenderer;

	public EnderEyeFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, EnderEyeEntityRenderer enderEyeEntityRenderer) {
		super(entityRendererIn);
		this.enderEyeEntityRenderer = enderEyeEntityRenderer;
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity ratEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (ratEntity.isSpy() && !ratEntity.isInvisible()) {
			this.enderEyeEntityRenderer.render(this.getEntityModel().getModel(this.getEntityModel().getModelResource(ratEntity)),
				ratEntity,
				partialTicks,
				RenderLayer.getEntityCutout(TEXTURE),
				matrixStackIn,
				bufferIn,
				bufferIn.getBuffer(RenderLayer.getEntityCutout(TEXTURE)),
				packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		}
	}
}
