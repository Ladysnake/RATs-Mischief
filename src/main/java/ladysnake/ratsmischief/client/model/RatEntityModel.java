package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class RatEntityModel extends GeoModel<RatEntity> {
	private static final Identifier MODEL = RatsMischief.id("geo/entity/rat.geo.json");
	private static final Identifier ANIMATION = RatsMischief.id("animations/entity/rat.animation.json");

	@Override
	public Identifier getModelResource(RatEntity rat) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(RatEntity rat) {
		return RatsMischiefUtils.getRatTexture(rat.getRatType(), rat.getRatColor());
	}

	@Override
	public Identifier getAnimationResource(RatEntity rat) {
		return ANIMATION;
	}

	@Override
	public void setCustomAnimations(RatEntity ratEntity, long instanceId, AnimationState<RatEntity> animationState) {
		super.setCustomAnimations(ratEntity, instanceId, animationState);

		if (!(ratEntity.isSniffing() || ratEntity.isEating() || ratEntity.isFlying())) {
			CoreGeoBone head = this.getAnimationProcessor().getBone("head");
			CoreGeoBone leftEar = this.getAnimationProcessor().getBone("leftear");
			CoreGeoBone rightEar = this.getAnimationProcessor().getBone("rightear");

			head.setRotX(MathHelper.clamp(-ratEntity.getPitch(), 0, 90) * ((float) Math.PI / 180F));
			leftEar.setRotX(MathHelper.clamp(ratEntity.getPitch(), -90, 0) * 1.4f * ((float) Math.PI / 180F));
			rightEar.setRotX(MathHelper.clamp(ratEntity.getPitch(), -90, 0) * 1.4f * ((float) Math.PI / 180F));

//            head.setRotationY(ratEntity.getHeadYaw() * ((float) Math.PI / 180F));
		}

		// sexually aroused rat
		if (ratEntity.isAroused()) {
			CoreGeoBone tail = this.getAnimationProcessor().getBone("tail");
			CoreGeoBone tailend = this.getAnimationProcessor().getBone("tailend");

			tail.setRotX((float) (-45 * Math.PI / 180));
			tailend.setRotX((float) (-30 * Math.PI / 180));
		}
	}
}
