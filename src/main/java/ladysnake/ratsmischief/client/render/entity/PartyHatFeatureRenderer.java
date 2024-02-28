package ladysnake.ratsmischief.client.render.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.vertex.VertexConsumer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Set;

public class PartyHatFeatureRenderer extends GeoRenderLayer<RatEntity> {
	public static final Set<RatEntity.Type> DISALLOWED_TYPES = Sets.immutableEnumSet(RatEntity.Type.RAT_KID, RatEntity.Type.BIGGIE_CHEESE, RatEntity.Type.REMY);
	public static Identifier[] TEXTURES;

	private final PartyHatEntityRenderer partyHatEntityRenderer;

	public PartyHatFeatureRenderer(GeoRenderer<RatEntity> entityRendererIn, PartyHatEntityRenderer partyHatEntityRenderer) {
		super(entityRendererIn);
		this.partyHatEntityRenderer = partyHatEntityRenderer;
	}

	@Override
	public void render(MatrixStack matrixStackIn, RatEntity ratEntity, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSourceIn, VertexConsumer bufferIn, float partialTicks, int packedLightIn, int packedOverlayIn) {
		if (RatsMischiefUtils.IS_BIRTHDAY && !DISALLOWED_TYPES.contains(ratEntity.getRatType())) {
			if (TEXTURES == null) {
				TEXTURES = new Identifier[RatEntity.PartyHat.values().length];
				for (RatEntity.PartyHat hat : RatEntity.PartyHat.values()) {
					TEXTURES[hat.ordinal()] = RatsMischief.id("textures/entity/birthday_hats/" + hat.toString().toLowerCase() + ".png");
				}
			}
			Identifier hatTexture = TEXTURES[ratEntity.getPartyHat().ordinal()];
			this.partyHatEntityRenderer.defaultRender(
				matrixStackIn,
				ratEntity,
				bufferSourceIn,
				RenderLayer.getEntityCutout(hatTexture),
				bufferIn,
				0.0F,
				partialTicks,
				packedLightIn
			);
		}
	}
}
