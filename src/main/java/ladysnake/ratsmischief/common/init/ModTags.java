package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

@SuppressWarnings("unused")
public interface ModTags {
	TagKey<Item> RAT_POUCHES = TagKey.of(RegistryKeys.ITEM, RatsMischief.id("rat_pouches"));
	TagKey<Item> REPAIRS_RAT_MASTER_ARMOR = TagKey.of(RegistryKeys.ITEM, RatsMischief.id("repairs_rat_master_armor"));

	static void initialize() {
		// NO-OP
	}
}
