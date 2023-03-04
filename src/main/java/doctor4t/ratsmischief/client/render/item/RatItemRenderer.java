package doctor4t.ratsmischief.client.render.item;

import doctor4t.ratsmischief.client.model.RatItemModel;
import doctor4t.ratsmischief.client.render.entity.EnderEyeFeatureRenderer;
import doctor4t.ratsmischief.client.render.entity.PartyHatFeatureRenderer;
import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.RatsMischiefUtils;
import doctor4t.ratsmischief.common.entity.RatEntity;
import doctor4t.ratsmischief.common.item.RatItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

public class RatItemRenderer extends GeoItemRenderer<RatItem> {
	public RatItemRenderer() {
		super(new RatItemModel());
	}

	@Override
	public void render(RatItem animatable, MatrixStack matrices, VertexConsumerProvider bufferSource, int packedLight, ItemStack stack) {
//		super.render(animatable, matrices, bufferSource, packedLight, stack);

		GeoModel model = this.modelProvider.getModel(this.modelProvider.getModelResource(animatable));
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
		Identifier ratTexture = RatsMischiefUtils.getRatTexture(ratType, ratName, ratColor);

		MinecraftClient.getInstance().getTextureManager().bindTexture(ratTexture);
		RenderLayer renderLayer = RenderLayer.getEntityCutout(ratTexture);

		this.render(model, animatable, 0.0F, renderLayer, matrices, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);

		// ender eye
		if (stack.getNbt() != null
				&& stack.getNbt().contains(RatsMischief.MOD_ID)
				&& stack.getNbt().getCompound(RatsMischief.MOD_ID).contains("rat")
				&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").contains("Spy")
				&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").getBoolean("Spy")) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(EnderEyeFeatureRenderer.TEXTURE);
			this.render(model, animatable, 0.0F, RenderLayer.getEntityCutout(EnderEyeFeatureRenderer.TEXTURE), matrices, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
		}

		// party hat
		if (RatsMischiefUtils.IS_BIRTHDAY && !PartyHatFeatureRenderer.DISALLOWED_TYPES.contains(ratType) && !(ratName != null && ratName.equalsIgnoreCase("remy"))) {
			if (stack.getNbt() != null
					&& stack.getNbt().contains(RatsMischief.MOD_ID)
					&& stack.getNbt().getCompound(RatsMischief.MOD_ID).contains("rat")
					&& stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").contains("PartyHat")) {
				String hat = stack.getNbt().getCompound(RatsMischief.MOD_ID).getCompound("rat").getString("PartyHat");
				Identifier hatTexture = PartyHatFeatureRenderer.TEXTURES[RatEntity.PartyHat.valueOf(hat).ordinal()];

				MinecraftClient.getInstance().getTextureManager().bindTexture(hatTexture);
				this.render(model, animatable, 0.0F, RenderLayer.getEntityCutout(hatTexture), matrices, bufferSource, null, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
			}
		}

		matrices.pop();
	}
}
