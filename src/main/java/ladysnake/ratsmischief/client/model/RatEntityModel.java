package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.Rats;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
    @Override
    public Identifier getModelLocation(RatEntity rat) {
        return new Identifier(Rats.MODID, "geo/entity/rat.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RatEntity rat) {
        if (rat.getRatType() == RatEntity.Type.RUSSIAN_BLUE && rat.hasCustomName() && rat.getCustomName().getString().toLowerCase().equals("remy")) {
            return new Identifier(Rats.MODID, "textures/entity/remy.png");
        } else {
            if (rat.getRatType() == RatEntity.Type.RAT_KID) {
                return new Identifier(Rats.MODID, "textures/entity/rat_kid_" + rat.getRatColor().getName().toLowerCase() + ".png");
            } else {
                return new Identifier(Rats.MODID, "textures/entity/" + rat.getRatType().toString().toLowerCase() + ".png");
            }
        }
    }

    @Override
    public Identifier getAnimationFileLocation(RatEntity rat) {
        return new Identifier(Rats.MODID, "animations/entity/rat.animation.json");
    }

    @Override
    public void setLivingAnimations(RatEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");

        if (head != null && !entity.isSniffing() && !entity.isEating()) {
            head.setRotationX(-entity.pitch * ((float) Math.PI / 180F));
//            head.setRotationY(entity.getHeadYaw() * ((float) Math.PI / 180F));
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