package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.client.model.ElytratEntityModel;
import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderer.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderer.geo.IGeoRenderer;

public class ElytratFeatureRenderer extends GeoLayerRenderer<RatEntity> {
    private final ElytratEntityRenderer elytratEntityRenderer;

    public ElytratFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, ElytratEntityRenderer elytratEntityRenderer) {
        super(entityRendererIn);
        this.elytratEntityRenderer = elytratEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, RatEntity ratEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (ratEntity.isElytrat()) {
            elytratEntityRenderer.render(ratEntity, ratEntity.bodyYaw, v, matrixStack, vertexConsumerProvider, light);
        }
    }
}
