package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

@SuppressWarnings("unused")
public interface ModTags {
	TagKey<Item> RAT_POUCHES = TagKey.of(RegistryKeys.ITEM, RatsMischief.id("rat_pouches"));

	static void initialize() {
		// NO-OP
	}
}
