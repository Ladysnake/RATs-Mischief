package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class PartyHatEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<RatEntity, R> {
	private Identifier texture;

	protected PartyHatEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}

	@Override
	public void render(R renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
		RenderLayer renderLayer = RenderLayer.getEntityCutout(this.texture);
		this.defaultRender(renderState, poseStack, bufferSource, renderLayer, bufferSource.getBuffer(renderLayer));
		this.texture = null;
	}


	public void setTexture(Identifier texture) {
		this.texture = texture;
	}
}
