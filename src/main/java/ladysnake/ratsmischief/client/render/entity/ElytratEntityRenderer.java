package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.client.model.ElytratEntityModel;
import ladysnake.ratsmischief.client.model.RatEntityModel;
import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Quaternion;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderer.geo.GeoEntityRenderer;

public class ElytratEntityRenderer extends GeoEntityRenderer<RatEntity> {
    public ElytratEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new ElytratEntityModel());
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
