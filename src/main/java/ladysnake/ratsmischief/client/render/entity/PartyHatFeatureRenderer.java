package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.renderers.geo.GeoLayerRenderer;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;

public class PartyHatFeatureRenderer extends GeoLayerRenderer<RatEntity> {
    private final PartyHatEntityRenderer partyHatEntityRenderer;
    Identifier partyHatLocation = null;

    public PartyHatFeatureRenderer(IGeoRenderer<RatEntity> entityRendererIn, PartyHatEntityRenderer partyHatEntityRenderer) {
        super(entityRendererIn);
        this.partyHatEntityRenderer = partyHatEntityRenderer;
    }

    @Override
    public void render(MatrixStack matrixStackIn, VertexConsumerProvider bufferIn, int packedLightIn, RatEntity ratEntity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (Mischief.IS_BIRTHDAY && (ratEntity.getRatType() != RatEntity.Type.RAT_KID && ratEntity.getRatType() != RatEntity.Type.BIGGIE_CHEESE && ratEntity.getRatType() != RatEntity.Type.JORATO && !(ratEntity.hasCustomName() && ratEntity.getCustomName().getString().toLowerCase().equals("remy")))) {
            this.partyHatLocation = new Identifier(Mischief.MODID, "textures/entity/birthday_hats/" + ratEntity.getPartyHat().toString().toLowerCase() + ".png");

            partyHatEntityRenderer.render(getEntityModel().getModel(new Identifier(Mischief.MODID, "geo/entity/rat.geo.json")),
                    ratEntity,
                    partialTicks,
                    RenderLayer.getEntityCutout(partyHatLocation),
                    matrixStackIn,
                    bufferIn,
                    bufferIn.getBuffer(RenderLayer.getEntityCutout(partyHatLocation)),
                    packedLightIn, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
    }
}
