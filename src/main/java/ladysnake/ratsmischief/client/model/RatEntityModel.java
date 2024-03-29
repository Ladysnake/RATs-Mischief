package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
	private static final Identifier MODEL = new Identifier(RatsMischief.MOD_ID, "geo/entity/rat.geo.json");
	private static final Identifier ANIMATION = new Identifier(RatsMischief.MOD_ID, "animations/entity/rat.animation.json");

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
	public void setCustomAnimations(RatEntity ratEntity, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(ratEntity, instanceId, animationEvent);

		IBone head = this.getAnimationProcessor().getBone("head");
		IBone leftEar = this.getAnimationProcessor().getBone("leftear");
		IBone rightEar = this.getAnimationProcessor().getBone("rightear");

		if (head != null && !ratEntity.isSniffing() && !ratEntity.isEating() && !ratEntity.isFlying()) {
			head.setRotationX(MathHelper.clamp(-ratEntity.getPitch(), 0, 90) * ((float) Math.PI / 180F));
			leftEar.setRotationX(MathHelper.clamp(ratEntity.getPitch(), -90, 0) * 1.4f * ((float) Math.PI / 180F));
			rightEar.setRotationX(MathHelper.clamp(ratEntity.getPitch(), -90, 0) * 1.4f * ((float) Math.PI / 180F));

//            head.setRotationY(ratEntity.getHeadYaw() * ((float) Math.PI / 180F));
		}

		// sexually aroused rat
		if (ratEntity.isAroused()) {
			software.bernie.geckolib3.core.processor.IBone tail = this.getAnimationProcessor().getBone("tail");
			software.bernie.geckolib3.core.processor.IBone tailend = this.getAnimationProcessor().getBone("tailend");

			tail.setRotationX((float) (-45 * Math.PI / 180));
			tailend.setRotationX((float) (-30 * Math.PI / 180));
		}

		if (ratEntity.isBaby()) {
			IBone root = this.getAnimationProcessor().getBone("root");
			if (root != null) {
				root.setScaleX(0.5f);
				root.setScaleY(0.5f);
				root.setScaleZ(0.5f);
			}
		}
	}
}
