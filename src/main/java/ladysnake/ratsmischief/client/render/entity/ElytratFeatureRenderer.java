package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

import java.util.Locale;

public class ElytratFeatureRenderer extends GeoLayerRenderer<RatEntity> {
    private static Identifier[] RAT_KID_TEXTURES;
    private static final Identifier DEFAULT_ELYTRAT = new Identifier(Mischief.MODID, "textures/entity/elytrat.png");

    private final ElytratEntityRenderer elytratEntityRenderer;

    public ElytratFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, ElytratEntityRenderer elytratEntityRenderer) {
        super(entityRendererIn);
        this.elytratEntityRenderer = elytratEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity rat, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (RAT_KID_TEXTURES == null) {
            RAT_KID_TEXTURES = new Identifier[16];
            for (DyeColor color : DyeColor.values()) {
                RAT_KID_TEXTURES[color.getId()] = new Identifier(Mischief.MODID, "textures/entity/rat_kid/rat_kid_" + color.getName().toLowerCase(Locale.ROOT) + "_elytrat.png");
            }
        }
        Identifier textureToUse = rat.getRatType().elytratTexture;
        if (rat.getRatType() == RatEntity.Type.RAT_KID) {
            textureToUse = RAT_KID_TEXTURES[rat.getRatColor().getId()];
        }
        else if (textureToUse == null) {
            textureToUse = DEFAULT_ELYTRAT;
        }

        if (rat.isElytrat()) {
            elytratEntityRenderer.render(getEntityModel().getModel(getEntityModel().getModelLocation(rat)),
                    rat,
                    partialTicks,
                    RenderLayer.getEntityCutout(textureToUse),
                    matrixStackIn,
                    bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEntityCutout(textureToUse)),
                    packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
    }
}
