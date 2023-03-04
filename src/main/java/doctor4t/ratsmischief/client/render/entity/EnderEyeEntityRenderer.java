package doctor4t.ratsmischief.client.render.entity;

import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class EnderEyeEntityRenderer extends GeoEntityRenderer<RatEntity> {
	protected EnderEyeEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}
}
