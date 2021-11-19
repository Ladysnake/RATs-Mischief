package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.Mischief;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ElytratEntityModel extends AnimatedGeoModel<RatEntity> {
    @Override
    public Identifier getModelLocation(RatEntity rat) {
        return new Identifier(Mischief.MODID, "geo/entity/elytrat.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RatEntity rat) {
        if (!rat.isSpecial() && rat.getRatType() != RatEntity.Type.GOLD) {
            return new Identifier(Mischief.MODID, "textures/entity/elytrat.png");
        } else {
            if (rat.getRatType() == RatEntity.Type.RAT_KID) {
                return new Identifier(Mischief.MODID, "textures/entity/rat_kid/rat_kid_" + rat.getRatColor().getName().toLowerCase() + "_elytrat.png");
            } else if (rat.isSpecial()) {
                return new Identifier(Mischief.MODID, "textures/entity/named/" + rat.getRatType().toString().toLowerCase() + "_elytrat.png");
            } else {
                return new Identifier(Mischief.MODID, "textures/entity/" + rat.getRatType().toString().toLowerCase() + "_elytrat.png");
            }
        }
    }

    @Override
    public Identifier getAnimationFileLocation(RatEntity rat) {
        return new Identifier(Mischief.MODID, "animations/entity/elytrat.animation.json");
    }

    @Override
    public void setLivingAnimations(RatEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);

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