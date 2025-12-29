package ladysnake.ratsmischief.client.render.item;

import ladysnake.ratsmischief.client.model.RatItemModel;
import ladysnake.ratsmischief.client.render.entity.EnderEyeFeatureRenderer;
import ladysnake.ratsmischief.client.render.entity.PartyHatFeatureRenderer;
import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

import java.util.Optional;

public class RatItemRenderer extends GeoItemRenderer<RatItem> {
	private Identifier currentRatTexture = null;
	private RenderLayer currentRenderLayer = null;

	public RatItemRenderer() {
		super(new RatItemModel());
	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
//		super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

		NbtCompound ratNbt = Optional.ofNullable(stack.getNbt()).map(nbt -> nbt.getCompound(RatsMischief.MOD_ID)).map(nbt -> nbt.getCompound("rat")).orElse(null);

		poseStack.push();
		poseStack.translate(0.0f, 0.01f, 0.0f);

		if (ratNbt != null && ratNbt.contains("Age") && ratNbt.getInt("Age") < 0) {
			poseStack.translate(0.25f, 0.25f, 0.25f);
			poseStack.scale(0.5f, 0.5f, 0.5f);
		}

//		String ratName = RatItem.getRatName(stack);

		RatEntity.Type ratType = RatItem.getRatType(stack);
		DyeColor ratColor = RatItem.getRatColor(stack);

		Identifier ratTexture = RatsMischiefUtils.getRatTexture(ratType, ratColor);
		this.currentRatTexture = ratTexture;

		this.bindTextureAndRender(ratTexture, stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);

		// ender eye
		if (ratNbt != null && ratNbt.contains("Spy") && ratNbt.getBoolean("Spy")) {
			this.bindTextureAndRender(EnderEyeFeatureRenderer.TEXTURE, stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
		}

		// party hat
		if (RatsMischiefUtils.IS_BIRTHDAY && !PartyHatFeatureRenderer.DISALLOWED_TYPES.contains(ratType)) {
			if (ratNbt != null && ratNbt.contains("PartyHat")) {
				String hat = ratNbt.getString("PartyHat");
				Identifier hatTexture = PartyHatFeatureRenderer.TEXTURES[RatEntity.PartyHat.valueOf(hat).ordinal()];

				this.bindTextureAndRender(hatTexture, stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
			}
		}

		poseStack.pop();
		this.currentRatTexture = null;
		this.currentRenderLayer = null;
	}

	private void bindTextureAndRender(Identifier texture, ItemStack stack, ModelTransformationMode transformType, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
		MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		this.currentRenderLayer = RenderLayer.getEntityCutout(texture);
		super.render(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
	}

	@Override
	public RenderLayer getRenderType(RatItem animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
		return this.currentRenderLayer == null ? super.getRenderType(animatable, texture, bufferSource, partialTick) : this.currentRenderLayer;
	}

	@Override
	public Identifier getTextureLocation(RatItem instance) {
		return this.currentRatTexture == null ? super.getTextureLocation(instance) : this.currentRatTexture;
	}
}
