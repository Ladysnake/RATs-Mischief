//package ladysnake.ratsmischief.client.render.item;
//
//import ladysnake.ratsmischief.client.RatsMischiefClient;
//import ladysnake.ratsmischief.common.RatsMischief;
//import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
//import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
//import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.render.VertexConsumerProvider;
//import net.minecraft.client.render.item.ItemRenderer;
//import net.minecraft.client.render.model.BakedModel;
//import net.minecraft.client.render.model.json.ModelTransformationMode;
//import net.minecraft.client.util.math.MatrixStack;
//import net.minecraft.item.ItemStack;
//import net.minecraft.resource.ResourceManager;
//import net.minecraft.util.Identifier;
//
//public class RatMasterMaskItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {
//	private static final Identifier RENDERER_ID = RatsMischief.id("rat_master_mask_renderer");
//	private ItemRenderer itemRenderer;
//	private BakedModel inventoryModel;
//	private BakedModel wornModel;
//
//	@Override
//	public Identifier getFabricId() {
//		return RENDERER_ID;
//	}
//
//	@Override
//	public void reload(ResourceManager manager) {
//		final MinecraftClient client = MinecraftClient.getInstance();
//		this.itemRenderer = client.getItemRenderer();
//		this.inventoryModel = client.getBakedModelManager().getModel(RatsMischiefClient.RAT_MASTER_MASK);
//		this.wornModel = client.getBakedModelManager().getModel(RatsMischiefClient.RAT_MASTER_MASK_WORN);
//	}
//
//	@Override
//	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
//		if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND || mode == ModelTransformationMode.FIXED) {
//			this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryModel);
//		} else {
//			this.itemRenderer.renderItem(stack, mode, mode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || mode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND, matrices, vertexConsumers, light, overlay, this.wornModel);
//		}
//	}
//}
