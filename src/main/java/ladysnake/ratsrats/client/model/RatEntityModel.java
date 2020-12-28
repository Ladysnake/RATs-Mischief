package ladysnake.ratsrats.client.model;

import ladysnake.ratsrats.common.Rats;
import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RatEntityModel extends AnimatedGeoModel<RatEntity> {
    @Override
    public Identifier getModelLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "geo/entity/rat.geo.json");
    }

    @Override
    public Identifier getTextureLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "textures/entity/grey_rat.png");
    }

    @Override
    public Identifier getAnimationFileLocation(RatEntity object) {
        return new Identifier(Rats.MODID, "animations/entity/rat.animation.json");
    }
}