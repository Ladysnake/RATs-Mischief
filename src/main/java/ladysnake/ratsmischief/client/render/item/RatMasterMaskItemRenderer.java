package ladysnake.ratsmischief.client.render.item;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.resource.loader.api.reloader.IdentifiableResourceReloader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class RatMasterMaskItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, IdentifiableResourceReloader {
	private final Identifier rendererId;
	private final Identifier itemId;
	private ItemRenderer itemRenderer;
	private BakedModel inventoryModel;
	private BakedModel wornModel;

	public RatMasterMaskItemRenderer(Identifier itemId) {
		this.rendererId = Identifier.tryParse(itemId + "_renderer");
		this.itemId = itemId;
	}

	@Override
	public @NotNull Identifier getQuiltId() {
		return this.rendererId;
	}

	@Override
	public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
		return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
			applyProfiler.startTick();
			applyProfiler.push("listener");
			final MinecraftClient client = MinecraftClient.getInstance();
			this.itemRenderer = client.getItemRenderer();
			this.inventoryModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.itemId, "inventory"));
			this.wornModel = client.getBakedModelManager().getModel(new ModelIdentifier(new Identifier(this.itemId.getNamespace(), this.itemId.getPath() + "_worn"), "inventory"));
			applyProfiler.pop();
			applyProfiler.endTick();
		}, applyExecutor);
	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.pop();
		matrices.push();
		if (mode == ModelTransformationMode.GUI || mode == ModelTransformationMode.GROUND || mode == ModelTransformationMode.FIXED) {
			this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryModel);
		} else {
			boolean leftHanded;
			switch (mode) {
				case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> leftHanded = true;
				default -> leftHanded = false;
			}
			this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.wornModel);
		}
	}
}
