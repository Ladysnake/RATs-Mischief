package ladysnake.ratsmischief.client.render.entity;

import ladysnake.ratsmischief.common.cca.RatHornsComponent;
import ladysnake.ratsmischief.common.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class GildedHornsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    public static final ItemStack HORNS = ModItems.RAT_BELLICIST_HORNS.getDefaultStack();
    private final HeldItemRenderer heldItemRenderer;

    public GildedHornsFeatureRenderer(FeatureRendererContext<T, M> context, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.heldItemRenderer = heldItemRenderer;
    }

    public void render(@NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        if (!RatHornsComponent.KEY.get(livingEntity).hasHorns()) return;
        matrixStack.push();
        this.getContextModel().getHead().rotate(matrixStack);
        matrixStack.translate(0.0F, -0.25F, 0.0F);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrixStack.scale(0.625F, -0.625F, -0.625F);
        this.heldItemRenderer.renderItem(livingEntity, HORNS, Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }
}