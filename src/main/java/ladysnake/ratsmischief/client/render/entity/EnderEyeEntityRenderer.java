package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EnderEyeEntityRenderer extends GeoEntityRenderer<RatEntity> {
	protected EnderEyeEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}
}
