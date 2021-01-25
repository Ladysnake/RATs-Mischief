package ladysnake.ratsrats.client.model;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.model.provider.data.EntityModelData;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
    @Override
    public Identifier getModelLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "geo/entity/rat.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "textures/entity/"+object.getRatType().toString().toLowerCase()+".png");
    }

    @Override
    public Identifier getAnimationFileLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "animations/entity/rat.animation.json");
    }

    @Override
    public void setLivingAnimations(RatEntity entity, Integer uniqueID, AnimationEvent customPredicate) {
        super.setLivingAnimations(entity, uniqueID, customPredicate);
        IBone head = this.getAnimationProcessor().getBone("head");
        IBone body = this.getAnimationProcessor().getBone("body");

        if (head != null) {
            head.setRotationX(-entity.pitch * ((float) Math.PI / 180F));
//            head.setRotationY(entity.getHeadYaw() * ((float) Math.PI / 180F));
        }
    }
}