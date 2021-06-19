package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ElytratEntityRenderer extends GeoEntityRenderer<RatEntity> {
    protected ElytratEntityRenderer(EntityRendererFactory.Context ctx, AnimatedGeoModel<RatEntity> modelProvider) {
        super(ctx, modelProvider);
    }

    @Override
    protected int getBlockLight(RatEntity rat, BlockPos blockPos) {
        if (rat.getRatType() == RatEntity.Type.RAT_KID && rat.getRatColor() == DyeColor.PURPLE) {
            return 15;
        } else {
            return super.getBlockLight(rat, blockPos);
        }
    }
}
