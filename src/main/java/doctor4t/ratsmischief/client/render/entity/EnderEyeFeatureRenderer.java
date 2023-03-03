package doctor4t.ratsmischief.client.render.entity;

import com.google.common.collect.Sets;
import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.RatsMischiefUtils;
import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Set;

public class EnderEyeFeatureRenderer extends GeoLayerRenderer<RatEntity> {
	private static Identifier TEXTURE = new Identifier(RatsMischief.MOD_ID, "");

	private final PartyHatEntityRenderer partyHatEntityRenderer;

	public EnderEyeFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, PartyHatEntityRenderer partyHatEntityRenderer) {
		super(entityRendererIn);
		this.partyHatEntityRenderer = partyHatEntityRenderer;
	}

	@Override
	public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity ratEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
		if (ratEntity.isSpy()) {
			partyHatEntityRenderer.render(getEntityModel().getModel(getEntityModel().getModelResource(ratEntity)),
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
