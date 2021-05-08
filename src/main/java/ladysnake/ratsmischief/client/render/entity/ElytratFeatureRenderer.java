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
    Identifier elytratLocation = null;

    public ElytratFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, ElytratEntityRenderer elytratEntityRenderer) {
        super(entityRendererIn);
        this.elytratEntityRenderer = elytratEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity ratEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
//        if (elytratLocation == null) {
            if (!ratEntity.hasCustomElytratTexture()) {
                this.elytratLocation = new Identifier(Mischief.MODID, "textures/entity/elytrat.png");
            } else {
                if (ratEntity.getRatType() == RatEntity.Type.RAT_KID) {
                    this.elytratLocation = new Identifier(Mischief.MODID, "textures/entity/rat_kid_" + ratEntity.getRatColor().getName().toLowerCase() + "_elytrat.png");
                } else {
                    this.elytratLocation = new Identifier(Mischief.MODID, "textures/entity/" + ratEntity.getRatType().toString().toLowerCase() + "_elytrat.png");
                }
            }
//        }

        if (ratEntity.isElytrat()) {
            elytratEntityRenderer.render(getEntityModel().getModel(new Identifier(Mischief.MODID,"geo/entity/rat.geo.json")),
                    ratEntity,
                    partialTicks,
                    RenderLayer.getEntityCutout(elytratLocation),
                    matrixStackIn,
                    bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEntityCutout(elytratLocation)),
                    packedLightIn, packedLightIn, 1, 1, 1, 1);
        }
    }
}
