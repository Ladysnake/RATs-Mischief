package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class PartyHatEntityRenderer extends GeoEntityRenderer<RatEntity> {
    protected PartyHatEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<RatEntity> modelProvider) {
        super(ctx, modelProvider);
    }
}
