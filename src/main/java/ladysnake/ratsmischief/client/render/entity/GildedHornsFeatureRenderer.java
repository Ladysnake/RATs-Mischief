//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package ladysnake.ratsmischief.client.render.entity;

import com.mojang.authlib.GameProfile;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class GildedHornsFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    private final float scaleX;
    private final float scaleY;
    private final float scaleZ;
    private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;
    private final HeldItemRenderer heldItemRenderer;

    public GildedHornsFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader modelLoader, HeldItemRenderer heldItemRenderer) {
        this(context, modelLoader, 1.0F, 1.0F, 1.0F, heldItemRenderer);
    }

    public GildedHornsFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader modelLoader, float scaleX, float scaleY, float scaleZ, HeldItemRenderer heldItemRenderer) {
        super(context);
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.scaleZ = scaleZ;
        this.headModels = SkullBlockEntityRenderer.getModels(modelLoader);
        this.heldItemRenderer = heldItemRenderer;
    }

    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l) {
        ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
        if (!itemStack.isEmpty()) {
            Item item = itemStack.getItem();
            matrixStack.push();
            matrixStack.scale(this.scaleX, this.scaleY, this.scaleZ);
            boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
            if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
                float m = 2.0F;
                float n = 1.4F;
                matrixStack.translate((double)0.0F, (double)0.03125F, (double)0.0F);
                matrixStack.scale(0.7F, 0.7F, 0.7F);
                matrixStack.translate((double)0.0F, (double)1.0F, (double)0.0F);
            }

            ((ModelWithHead)this.getContextModel()).getHead().rotate(matrixStack);
            if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
                float m = 1.1875F;
                matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
                if (bl) {
                    matrixStack.translate((double)0.0F, (double)0.0625F, (double)0.0F);
                }

                GameProfile gameProfile = null;
                if (itemStack.hasNbt()) {
                    NbtCompound nbtCompound = itemStack.getNbt();
                    if (nbtCompound.contains("SkullOwner", 10)) {
                        gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
                    }
                }

                matrixStack.translate((double)-0.5F, (double)0.0F, (double)-0.5F);
                SkullBlock.SkullType skullType = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType();
                SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.get(skullType);
                RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, gameProfile);
                SkullBlockEntityRenderer.renderSkull((Direction)null, 180.0F, f, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
            } else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
                translate(matrixStack, bl);
                this.heldItemRenderer.renderItem(livingEntity, itemStack, Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
            }

            matrixStack.pop();
        }
    }

    public static void translate(MatrixStack matrices, boolean villager) {
        float f = 0.625F;
        matrices.translate((double)0.0F, (double)-0.25F, (double)0.0F);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
        matrices.scale(0.625F, -0.625F, -0.625F);
        if (villager) {
            matrices.translate((double)0.0F, (double)0.1875F, (double)0.0F);
        }

    }
}
