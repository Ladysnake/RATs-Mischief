package ladysnake.ratsmischief.client.render.entity;

import com.google.common.collect.Sets;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
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
	static {
		TEXTURES = new Identifier[RatEntity.PartyHat.values().length];
		for (RatEntity.PartyHat hat : RatEntity.PartyHat.values()) {
			TEXTURES[hat.ordinal()] = RatsMischief.id("textures/entity/birthday_hats/" + hat.toString().toLowerCase() + ".png");
		}
	}

	private final PartyHatEntityRenderer partyHatEntityRenderer;

	public PartyHatFeatureRenderer(GeoRenderer<RatEntity> entityRendererIn, PartyHatEntityRenderer partyHatEntityRenderer) {
		super(entityRendererIn);
		this.partyHatEntityRenderer = partyHatEntityRenderer;
	}

	@Override
	public void render(MatrixStack poseStack, RatEntity animatable, BakedGeoModel bakedModel, RenderLayer renderType, VertexConsumerProvider bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		if (RatsMischiefUtils.IS_BIRTHDAY && !DISALLOWED_TYPES.contains(animatable.getRatType())) {
			this.partyHatEntityRenderer.setTexture(TEXTURES[animatable.getPartyHat().ordinal()]);
			this.partyHatEntityRenderer.render(
				animatable,
				animatable.getYaw(),
				partialTick,
				poseStack,
				bufferSource,
				packedLight
			);
		}
	}
}
