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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class RatItemRenderer extends GeoItemRenderer<RatItem> {
	private ThreadLocal<Identifier> currentTexture = new ThreadLocal<>();

	public RatItemRenderer() {
		super(new RatItemModel());
	}

	@Override
	public void render(ItemStack stack, ModelTransformationMode transformType, MatrixStack matrices, VertexConsumerProvider bufferSource, int packedLight, int packedOverlay) {
//		super.render(stack, transformType, matrices, bufferSource, packedLight, packedOverlay);

		this.animatable = (RatItem) stack.getItem();
		this.currentItemStack = stack;
		this.renderPerspective = transformType;

		matrices.push();
		matrices.translate(0.5f, 0.51f, 0.5f);

		if (stack.getNbt() != null
			&& stack.getNbt().contains(RatsMischief.MOD_ID)
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).contains("rat")
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").contains("Age")
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").getInt("Age") < 0) {
			matrices.translate(0, 0, -0.1f);
			matrices.scale(0.5f, 0.5f, 0.5f);
		}

		RatEntity.Type ratType = RatItem.getRatType(stack);
		String ratName = RatItem.getRatName(stack);
		DyeColor ratColor = RatItem.getRatColor(stack);
		Identifier ratTexture = RatsMischiefUtils.getRatTexture(ratType, ratColor);

		currentTexture.set(ratTexture);

		MinecraftClient.getInstance().getTextureManager().bindTexture(ratTexture);
		RenderLayer renderLayer = RenderLayer.getEntityCutout(ratTexture);

		this.defaultRender(matrices, this.animatable, bufferSource, renderLayer, bufferSource.getBuffer(renderLayer), 0, MinecraftClient.getInstance().getTickDelta(), packedLight);

		// ender eye
		if (stack.getNbt() != null
			&& stack.getNbt().contains(RatsMischief.MOD_ID)
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).contains("rat")
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").contains("Spy")
			&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").getBoolean("Spy")) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(EnderEyeFeatureRenderer.TEXTURE);
			this.defaultRender(matrices, this.animatable, bufferSource, RenderLayer.getEntityCutout(EnderEyeFeatureRenderer.TEXTURE), bufferSource.getBuffer(renderLayer), 0, MinecraftClient.getInstance().getTickDelta(), packedLight);
		}

		// party hat
		if (RatsMischiefUtils.IS_BIRTHDAY && !PartyHatFeatureRenderer.DISALLOWED_TYPES.contains(ratType)) {
			if (stack.getNbt() != null
				&& stack.getNbt().contains(RatsMischief.MOD_ID)
				&& stack.getNbt().getCompound(RatsMischief.MOD_ID).contains("rat")
				&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").contains("PartyHat")) {
				String hat = stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").getString("PartyHat");
				Identifier hatTexture = PartyHatFeatureRenderer.TEXTURES[RatEntity.PartyHat.valueOf(hat).ordinal()];

				MinecraftClient.getInstance().getTextureManager().bindTexture(hatTexture);
				this.defaultRender(matrices, this.animatable, bufferSource, RenderLayer.getEntityCutout(hatTexture), bufferSource.getBuffer(renderLayer), 0, MinecraftClient.getInstance().getTickDelta(), packedLight);
			}
		}

		matrices.pop();
		currentTexture.remove();
	}

	@Override
	public Identifier getTextureLocation(RatItem instance) {
		return currentTexture.get() != null ? currentTexture.get() : super.getTextureLocation(instance);
	}
}
