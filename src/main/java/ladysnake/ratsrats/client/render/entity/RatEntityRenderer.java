package ladysnake.ratsrats.client.render.entity;

import ladysnake.ratsrats.client.model.RatEntityModel;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class RatEntityRenderer extends GeoEntityRenderer<RatEntity> {
    public RatEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new RatEntityModel());
        this.shadowRadius = 0.35f;
    }
}
