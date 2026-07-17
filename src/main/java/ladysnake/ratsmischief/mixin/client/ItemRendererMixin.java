package ladysnake.ratsmischief.mixin.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tessellator;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
	@Shadow
	@Final
	private TextureManager textureManager;

	@Shadow
	public abstract void renderItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model);

	@Shadow
	public float zOffset;

	@Shadow
	protected abstract void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha);

	@Inject(method = "renderGuiItemOverlay(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "TAIL"))
	public void ratsmischief$koPouchOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
		if (stack.getItem() instanceof RatPouchItem && RatPouchItem.hasKos(stack)) {
			MatrixStack matrixStack = new MatrixStack();
			String string = "☠";
			matrixStack.translate(0.0, 0.0, (this.zOffset + 200.0F));
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBufferBuilder());
			renderer.draw(
				string,
				(float) (x + 19 - 2 - renderer.getWidth(string)),
				(float) (y + 6 + 3),
				0xEE0000,
				true,
				matrixStack.peek().getModel(),
				immediate,
				false,
				0,
				LightmapTextureManager.MAX_LIGHT_COORDINATE
			);
			immediate.draw();
		}
	}
}
