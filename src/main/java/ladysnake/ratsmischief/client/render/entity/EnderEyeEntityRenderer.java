package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class EnderEyeEntityRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoEntityRenderer<RatEntity, R>{
	protected EnderEyeEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}

}
