package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

import java.util.Locale;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
    private static final Identifier MODEL_LOCATION = new Identifier(Mischief.MODID, "geo/entity/rat.geo.json");
    private static final Identifier ANIMATION_LOCATION = new Identifier(Mischief.MODID, "animations/entity/rat.animation.json");

    private static Identifier[] RAT_KID_TEXTURES;
    private static final Identifier REMY_TEXTURE = new Identifier(Mischief.MODID, "textures/entity/named/remy.png");

    @Override
    public Identifier getModelLocation(RatEntity rat) {
        return MODEL_LOCATION;
    }

    @Override
    public Identifier getTextureLocation(RatEntity rat) {
        if (RAT_KID_TEXTURES == null) {
            RAT_KID_TEXTURES = new Identifier[16];
            for (DyeColor color : DyeColor.values()) {
                RAT_KID_TEXTURES[color.getId()] = new Identifier(Mischief.MODID, "textures/entity/rat_kid/rat_kid_" + color.getName().toLowerCase(Locale.ROOT) + ".png");
            }
        }
        if (rat.getRatType() == RatEntity.Type.RUSSIAN_BLUE && rat.hasCustomName() && rat.getCustomName().getString().equalsIgnoreCase("remy")) {
            return REMY_TEXTURE;
        }
        else if (rat.getRatType() == RatEntity.Type.RAT_KID) {
            return RAT_KID_TEXTURES[rat.getRatColor().getId()];
        } else {
            return rat.getRatType().ratTexture;
        }
    }

    @Override
    public Identifier getAnimationFileLocation(RatEntity rat) {
        return ANIMATION_LOCATION;
    }

    @Override
    public void setLivingAnimations(RatEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

        IBone head = this.getAnimationProcessor().getBone("head");
        IBone rocket = this.getAnimationProcessor().getBone("rocket");

        IBone leftWing = this.getAnimationProcessor().getBone("left");
        IBone rightWing = this.getAnimationProcessor().getBone("right");

        if (entity.isFlying()) {
            if (leftWing != null && rightWing != null) {
                leftWing.setRotationY((float) MathHelper.clamp(-(entity.getVelocity().getY() * 2) - 1f, -1, 0));
                rightWing.setRotationY((float) MathHelper.clamp((entity.getVelocity().getY() * 2) + 1f, 0, 1));
            }

            IBone body = this.getAnimationProcessor().getBone("root");
            if (body != null) {
                body.setRotationX((float) entity.getVelocity().getY());
            }

            if (rocket != null) {
                rocket.setHidden(false);
            }
        } else {
            if (head != null && !entity.isSniffing() && !entity.isEating()) {
                head.setRotationX(-entity.getPitch() * ((float) Math.PI / 180F));

                if (rocket != null) {
                    rocket.setHidden(true);
                }
//            head.setRotationY(entity.getHeadYaw() * ((float) Math.PI / 180F));
            }
        }

        if (entity.isBaby()) {
            IBone root = this.getAnimationProcessor().getBone("root");
            if (root != null) {
                root.setScaleX(0.5f);
                root.setScaleY(0.5f);
                root.setScaleZ(0.5f);
            }
        }
    }
}