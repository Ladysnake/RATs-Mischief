package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderer.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderer.geo.IGeoRenderer;

public class ElytratFeatureRenderer extends GeoLayerRenderer<RatEntity> {
    private final ElytratEntityRenderer elytratEntityRenderer;
    Identifier elytratLocation = new Identifier(Mischief.MODID,"textures/entity/elytrat.png");
    Identifier modelLocation = new Identifier(Mischief.MODID,"geo/entity/elytrat.geo.json");

    public ElytratFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, ElytratEntityRenderer elytratEntityRenderer) {
        super(entityRendererIn);
        this.elytratEntityRenderer = elytratEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity ratEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (ratEntity.isElytrat()) {
            elytratEntityRenderer.render(getEntityModel().getModel(new Identifier(Mischief.MODID,"geo/entity/rat.geo.json")),
                    ratEntity,
                    partialTicks,
                    RenderLayer.getEntityCutout(elytratLocation),
                    matrixStackIn,
                    bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEntityCutout(elytratLocation)),
                    packedLightIn, 1, 1, 1, 1, 1);

        }
    }
}
