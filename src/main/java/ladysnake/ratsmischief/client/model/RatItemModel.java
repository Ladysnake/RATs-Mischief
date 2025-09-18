package ladysnake.ratsmischief.client.model;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

public class RatItemModel extends GeoModel<RatItem> {
	private static final Identifier MODEL = RatsMischief.id("geo/item/rat.geo.json");
	private static final Identifier DEFAULT_TEXTURE = RatsMischief.id("textures/entity/wild.png");
	private static final Identifier ANIMATION = RatsMischief.id("animations/item/rat.animation.json");

	public static final DataTicket<NbtCompound> RAT_DATA = DataTicket.create("rat_data", NbtCompound.class);

	@Override
	public Identifier getModelResource(GeoRenderState renderState) {
		return MODEL;
	}

	@Override
	public Identifier getTextureResource(GeoRenderState renderState) {
		return DEFAULT_TEXTURE;
	}

	@Override
	public Identifier getAnimationResource(RatItem ratItem) {
		return ANIMATION;
	}
}
