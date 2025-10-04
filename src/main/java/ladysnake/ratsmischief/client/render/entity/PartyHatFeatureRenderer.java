package ladysnake.ratsmischief.client.render.entity;

import com.google.common.collect.Sets;
import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Set;

public class PartyHatFeatureRenderer<R extends LivingEntityRenderState & GeoRenderState> extends GeoRenderLayer<RatEntity, Void, R> {
	public static final Set<RatEntity.Type> DISALLOWED_TYPES = Sets.immutableEnumSet(RatEntity.Type.RAT_KID, RatEntity.Type.BIGGIE_CHEESE, RatEntity.Type.REMY);
	public static Identifier[] TEXTURES;
	static {
		TEXTURES = new Identifier[RatEntity.PartyHat.values().length];
		for (RatEntity.PartyHat hat : RatEntity.PartyHat.values()) {
			TEXTURES[hat.ordinal()] = RatsMischief.id("textures/entity/birthday_hats/" + hat.toString().toLowerCase() + ".png");
		}
	}

	private final PartyHatEntityRenderer<R> partyHatEntityRenderer;

	public PartyHatFeatureRenderer(GeoEntityRenderer<RatEntity, R> entityRendererIn, PartyHatEntityRenderer<R> partyHatEntityRenderer) {
		super(entityRendererIn);
		this.partyHatEntityRenderer = partyHatEntityRenderer;
	}

	@Override
	public void render(R renderState, MatrixStack poseStack, BakedGeoModel bakedModel, @Nullable RenderLayer renderType, VertexConsumerProvider bufferSource, @Nullable VertexConsumer buffer, int packedLight, int packedOverlay, int renderColor) {
		if (RatsMischiefUtils.IS_BIRTHDAY && !DISALLOWED_TYPES.contains(renderState.getGeckolibData(RatEntityModel.TYPE_TICKET))) {
			this.partyHatEntityRenderer.setTexture(TEXTURES[renderState.getGeckolibData(RatEntityModel.PARTY_HAT_TEXTURE)]);
			this.partyHatEntityRenderer.render(renderState, poseStack, bufferSource, packedLight);
		}
	}
}
