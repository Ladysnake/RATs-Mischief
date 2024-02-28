package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class RatItemModel extends GeoModel<RatItem> {
	private static final Identifier MODEL = RatsMischief.id("geo/item/rat.geo.json");
	private static final Identifier DEFAULT_TEXTURE = RatsMischief.id("textures/entity/wild.png");
	private static final Identifier ANIMATION = RatsMischief.id("animations/item/rat.animation.json");

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
