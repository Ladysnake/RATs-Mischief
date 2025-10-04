package ladysnake.ratsmischief.client.render.item;

import ladysnake.ratsmischief.client.model.RatItemModel;
import ladysnake.ratsmischief.client.render.entity.EnderEyeFeatureRenderer;
import ladysnake.ratsmischief.client.render.entity.PartyHatFeatureRenderer;
import ladysnake.ratsmischief.common.RatsMischiefUtils;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RatItemRenderer extends GeoItemRenderer<RatItem> {
	private Identifier currentRatTexture = null;
	private RenderLayer currentRenderLayer = null;

	public RatItemRenderer() {
		super(new RatItemModel());
	}

	@Override
	public void render(GeoRenderState renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource) {
		NbtCompound ratNbt = renderState.getGeckolibData(RatItemModel.RAT_DATA);

		poseStack.push();
		poseStack.translate(0.0f, 0.01f, 0.0f);

		if (ratNbt.getInt("Age", 0) < 0) {
			poseStack.translate(0, 0, -0.1f);
			poseStack.translate(0.0f, 0.0f, 0.10f);
		}

//		String ratName = RatItem.getRatName(stack);

		RatEntity.Type ratType = RatItem.getRatType(ratNbt);
		DyeColor ratColor = RatItem.getRatColor(ratNbt);

		Identifier ratTexture = RatsMischiefUtils.getRatTexture(ratType, ratColor);
		this.currentRatTexture = ratTexture;

		this.bindTextureAndRender(ratTexture, renderState, poseStack, bufferSource);

		// ender eye
		if (ratNbt.getBoolean("Spy", false)) {
			this.bindTextureAndRender(EnderEyeFeatureRenderer.TEXTURE, renderState, poseStack, bufferSource);
		}

		// party hat
		if (RatsMischiefUtils.IS_BIRTHDAY && !PartyHatFeatureRenderer.DISALLOWED_TYPES.contains(ratType)) {
			if (ratNbt.contains("PartyHat")) {
				String hat = ratNbt.getString("PartyHat", "");
				Identifier hatTexture = PartyHatFeatureRenderer.TEXTURES[RatEntity.PartyHat.valueOf(hat).ordinal()];

				this.bindTextureAndRender(hatTexture, renderState, poseStack, bufferSource);
			}
		}

		poseStack.pop();
		this.currentRatTexture = null;
		this.currentRenderLayer = null;
	}

	@Override
	public void addRenderData(RatItem animatable, RenderData relatedObject, GeoRenderState renderState) {
		renderState.addGeckolibData(RatItemModel.RAT_DATA, relatedObject.itemStack().contains(ModDataComponents.RAT_ENTITY_DATA) ? relatedObject.itemStack().get(ModDataComponents.RAT_ENTITY_DATA).ratTag() : new NbtCompound());
	}

	private void bindTextureAndRender(Identifier texture, GeoRenderState renderState, MatrixStack poseStack, VertexConsumerProvider bufferSource) {
		//MinecraftClient.getInstance().getTextureManager().bindTexture(texture);
		this.currentRenderLayer = RenderLayer.getEntityCutout(texture);
		super.render(renderState, poseStack, bufferSource);
	}

	@Override
	public @Nullable RenderLayer getRenderType(GeoRenderState renderState, Identifier texture) {
		return this.currentRenderLayer == null ? super.getRenderType(renderState, texture) : this.currentRenderLayer;
	}

	@Override
	public Identifier getTextureLocation(GeoRenderState renderState) {
		return this.currentRatTexture == null ? super.getTextureLocation(renderState) : this.currentRatTexture;
	}

}
