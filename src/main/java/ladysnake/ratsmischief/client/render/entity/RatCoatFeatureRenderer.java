package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.renderer.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderer.geo.IGeoRenderer;

public class RatCoatFeatureRenderer extends GeoLayerRenderer<RatEntity> {
    private final RatCoatEntityRenderer ratCoatEntityRenderer;

    public RatCoatFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, RatCoatEntityRenderer ratCoatEntityRenderer) {
        super(entityRendererIn);
        this.ratCoatEntityRenderer = ratCoatEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, RatEntity ratEntity, float v, float v1, float v2, float v3, float v4, float v5) {
        ratCoatEntityRenderer.render(ratEntity, ratEntity.yaw, v, matrixStack, vertexConsumerProvider, light);
    }
}
