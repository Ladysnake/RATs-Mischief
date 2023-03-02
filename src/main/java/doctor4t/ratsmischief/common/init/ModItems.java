package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.item.RatPouchItem;
import doctor4t.ratsmischief.common.item.RatStaffItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	//	Item MOD_ITEM = createItem("mod_item", new ModItem(new QuiltItemSettings()));
	public static Item RAT_SPAWN_EGG = createItem("rat_spawn_egg", new SpawnEggItem(ModEntities.RAT, 0x1A1A1A, 0xF2ADA1, new QuiltItemSettings().group(ItemGroup.MISC)));

	public static Item LEATHER_RAT_POUCH = createItem("leather_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 5));
	public static Item TWISTED_RAT_POUCH = createItem("twisted_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 10));
	public static Item PURPUR_RAT_POUCH = createItem("purpur_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 20));

	public static Item HARVEST_STAFF = createItem("harvest_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.HARVEST));
	public static Item COLLECTION_STAFF = createItem("collection_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.COLLECT));
	public static Item LOVE_STAFF = createItem("love_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.LOVE));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(RatsMischief.MOD_ID, name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registry.ITEM, ITEMS.get(item), item);
//			ModItemGroup.addToItemGroup(ModItemGroup.MOD_ITEMS, item);
		});
	}
}
