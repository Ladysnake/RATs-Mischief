package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PartyHatEntityRenderer extends GeoEntityRenderer<RatEntity> {
	private Identifier texture;

	protected PartyHatEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}

	@Override
	public void render(RatEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		this.animatable = entity;

		RenderLayer renderLayer = RenderLayer.getEntityCutout(this.texture);
		this.defaultRender(poseStack, entity, bufferSource, renderLayer, bufferSource.getBuffer(renderLayer), entityYaw, partialTick, packedLight);
		this.texture = null;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
