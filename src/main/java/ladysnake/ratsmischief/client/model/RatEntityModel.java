package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.animatable.processing.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RatEntityModel extends GeoModel<RatEntity> {
	private static final Identifier MODEL = RatsMischief.id("entity/rat");
	private static final Identifier ANIMATION = RatsMischief.id("entity/rat");

	public static final DataTicket<RatEntity.Type> TYPE_TICKET = DataTicket.create("rat_type", RatEntity.Type.class);
	public static final DataTicket<DyeColor> DYE_COLOR_TICKET = DataTicket.create("rat_color", DyeColor.class);
	public static final DataTicket<Boolean> SNIFFING_TICKET = DataTicket.create("is_rat_sniffing", Boolean.class);
	public static final DataTicket<Boolean> EATING_TICKET = DataTicket.create("is_rat_eating", Boolean.class);
	public static final DataTicket<Boolean> FLYING_TICKET = DataTicket.create("is_rat_flying", Boolean.class);
	public static final DataTicket<Boolean> AROUSED_TICKET = DataTicket.create("is_rat_aroused", Boolean.class);
	public static final DataTicket<Boolean> SITTING_TICKET = DataTicket.create("is_rat_sitting", Boolean.class);
	public static final DataTicket<Boolean> SPY_TICKET = DataTicket.create("is_rat_spy", Boolean.class);
	public static final DataTicket<Integer> PARTY_HAT_TEXTURE = DataTicket.create("party_hat", Integer.class);


	@Override
	public Identifier getModelResource(GeoRenderState rat) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState rat) {
		return RatsMischiefUtils.getRatTexture(rat.getGeckolibData(TYPE_TICKET), rat.getGeckolibData(DYE_COLOR_TICKET));
	}

	@Override
	public Identifier getAnimationResource(RatEntity rat) {
		return ANIMATION;
	}

	@Override
	public void setCustomAnimations(AnimationState<RatEntity> animationState) {
		if (!(animationState.getData(SNIFFING_TICKET) || animationState.getData(EATING_TICKET) || animationState.getData(FLYING_TICKET))) {
			GeoBone head = this.getAnimationProcessor().getBone("head");
			GeoBone leftEar = this.getAnimationProcessor().getBone("leftear");
			GeoBone rightEar = this.getAnimationProcessor().getBone("rightear");

			head.setRotX(MathHelper.clamp(-animationState.renderState().getGeckolibData(DataTickets.ENTITY_PITCH), 0, 90) * ((float) Math.PI / 180F));
			leftEar.setRotX(MathHelper.clamp(animationState.renderState().getGeckolibData(DataTickets.ENTITY_PITCH), -90, 0) * 1.4f * ((float) Math.PI / 180F));
			rightEar.setRotX(MathHelper.clamp(animationState.renderState().getGeckolibData(DataTickets.ENTITY_PITCH), -90, 0) * 1.4f * ((float) Math.PI / 180F));

//            head.setRotationY(ratEntity.getHeadYaw() * ((float) Math.PI / 180F));
		}

		// sexually aroused rat
		if (animationState.getData(AROUSED_TICKET)) {
			GeoBone tail = this.getAnimationProcessor().getBone("tail");
			GeoBone tailend = this.getAnimationProcessor().getBone("tailend");

			tail.setRotX((float) (-45 * Math.PI / 180));
			tailend.setRotX((float) (-30 * Math.PI / 180));
		}
	}
}
