package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PartyHatEntityRenderer extends GeoEntityRenderer<RatEntity> {
	protected PartyHatEntityRenderer(EntityRendererFactory.Context ctx, GeoModel<RatEntity> modelProvider) {
		super(ctx, modelProvider);
	}
}
