package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class RatItemModel extends AnimatedGeoModel<RatItem> {
	private static final Identifier MODEL = new Identifier(RatsMischief.MOD_ID, "geo/item/rat.geo.json");
	private static final Identifier DEFAULT_TEXTURE = new Identifier(RatsMischief.MOD_ID, "textures/entity/wild.png");
	private static final Identifier ANIMATION = new Identifier(RatsMischief.MOD_ID, "animations/item/rat.animation.json");

	@Override
	public Identifier getModelResource(RatItem rat) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(RatItem ratItem) {
		return DEFAULT_TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(RatItem ratItem) {
		return ANIMATION;
	}
}
