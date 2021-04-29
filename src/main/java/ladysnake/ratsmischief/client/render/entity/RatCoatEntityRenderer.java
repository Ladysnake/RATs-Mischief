package ladysnake.ratsmischief.client.render.entity;

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

public class RatCoatEntityRenderer extends GeoEntityRenderer<RatEntity> {
    public RatCoatEntityRenderer(EntityRenderDispatcher renderManager) {
        super(renderManager, new RatEntityModel());
    }

    // variables needed for later
    private ItemStack itemStack;
    private VertexConsumerProvider vertexConsumerProvider;
    private Identifier ratTexture;

    @Override
    public void renderEarly(RatEntity ratEntity, MatrixStack stackIn, float ticks, VertexConsumerProvider vertexConsumerProvider, VertexConsumer vertexBuilder, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float partialTicks) {
        this.itemStack = ratEntity.getEquippedStack(EquipmentSlot.MAINHAND);
        this.vertexConsumerProvider = vertexConsumerProvider;
        this.ratTexture = this.getTextureLocation(ratEntity);

        super.renderEarly(ratEntity, stackIn, ticks, vertexConsumerProvider, vertexBuilder, packedLightIn, packedOverlayIn, red,
                green, blue, partialTicks);
    }

    @Override
    public void renderRecursively(GeoBone bone, MatrixStack stack, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.renderRecursively(bone, stack, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
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
