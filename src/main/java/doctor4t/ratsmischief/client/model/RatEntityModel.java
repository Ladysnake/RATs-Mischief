package doctor4t.ratsmischief.client.model;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Locale;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
	private static final Identifier MODEL_LOCATION = new Identifier(RatsMischief.MOD_ID, "geo/entity/rat.geo.json");
	private static final Identifier ANIMATION_LOCATION = new Identifier(RatsMischief.MOD_ID, "animations/entity/rat.animation.json");
	private static final Identifier REMY_TEXTURE = new Identifier(RatsMischief.MOD_ID, "textures/entity/named/remy.png");
	private static Identifier[] RAT_KID_TEXTURES;

	@Override
	public Identifier getModelResource(RatEntity rat) {
		return MODEL_LOCATION;
	}

	@Override
	public Identifier getTextureResource(RatEntity rat) {
		if (RAT_KID_TEXTURES == null) {
			RAT_KID_TEXTURES = new Identifier[16];
			for (DyeColor color : DyeColor.values()) {
				RAT_KID_TEXTURES[color.getId()] = new Identifier(RatsMischief.MOD_ID, "textures/entity/rat_kid/rat_kid_" + color.getName().toLowerCase(Locale.ROOT) + ".png");
			}
		}
		if (rat.getRatType() == RatEntity.Type.RUSSIAN_BLUE && rat.hasCustomName() && rat.getCustomName().getString().equalsIgnoreCase("remy")) {
			return REMY_TEXTURE;
		} else if (rat.getRatType() == RatEntity.Type.RAT_KID) {
			return RAT_KID_TEXTURES[rat.getRatColor().getId()];
		} else {
			return rat.getRatType().ratTexture;
		}
	}

	@Override
	public Identifier getAnimationResource(RatEntity rat) {
		return ANIMATION_LOCATION;
	}

	@Override
	public void setCustomAnimations(RatEntity ratEntity, int instanceId, AnimationEvent animationEvent) {
		super.setCustomAnimations(ratEntity, instanceId, animationEvent);

		IBone head = this.getAnimationProcessor().getBone("head");
		IBone leftEar = this.getAnimationProcessor().getBone("leftear");
		IBone rightEar = this.getAnimationProcessor().getBone("rightear");
		if (head != null && !ratEntity.isSniffing() && !ratEntity.isEating()) {
			head.setRotationX(-ratEntity.getPitch() * ((float) Math.PI / 180F));
			leftEar.setRotationX(ratEntity.getPitch()*1.4f * ((float) Math.PI / 180F));
			rightEar.setRotationX(ratEntity.getPitch()*1.4f * ((float) Math.PI / 180F));

//            head.setRotationY(ratEntity.getHeadYaw() * ((float) Math.PI / 180F));
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
