package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.item.RatItem;
import doctor4t.ratsmischief.common.item.RatPouchItem;
import doctor4t.ratsmischief.common.item.RatStaffItem;
import doctor4t.ratsmischief.common.item.RattyArmorItem;
import net.minecraft.entity.EquipmentSlot;
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
	Item RAT_SPAWN_EGG = createItem("rat_spawn_egg", new SpawnEggItem(ModEntities.RAT, 0x473935, 0x0F090B, new QuiltItemSettings().group(ItemGroup.MISC)));

	Item LEATHER_RAT_POUCH = createItem("leather_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 5));
	Item TWISTED_RAT_POUCH = createItem("twisted_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 10));
	Item PURPUR_RAT_POUCH = createItem("purpur_rat_pouch", new RatPouchItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), 20));

	Item HARVEST_STAFF = createItem("harvest_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.HARVEST));
	Item COLLECTION_STAFF = createItem("collection_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.COLLECT));
	Item LOVE_STAFF = createItem("love_staff", new RatStaffItem((new QuiltItemSettings()).group(ItemGroup.TOOLS).maxCount(1), RatStaffItem.Action.LOVE));
	Item RAT = createItem("rat", new RatItem((new QuiltItemSettings()).maxCount(1)));

	Item RATTY_HOOD = createItem("ratty_hood", new RattyArmorItem(RattyArmorItem.RattyArmorMaterial.INSTANCE, EquipmentSlot.HEAD, (new QuiltItemSettings()).group(ItemGroup.COMBAT)));
	Item RATTY_CHESTPLATE = createItem("ratty_chestplate", new RattyArmorItem(RattyArmorItem.RattyArmorMaterial.INSTANCE, EquipmentSlot.CHEST, (new QuiltItemSettings()).group(ItemGroup.COMBAT)));
	Item RATTY_LEGGINGS = createItem("ratty_leggings", new RattyArmorItem(RattyArmorItem.RattyArmorMaterial.INSTANCE, EquipmentSlot.LEGS, (new QuiltItemSettings()).group(ItemGroup.COMBAT)));
	Item RATTY_BOOTS = createItem("ratty_boots", new RattyArmorItem(RattyArmorItem.RattyArmorMaterial.INSTANCE, EquipmentSlot.FEET, (new QuiltItemSettings()).group(ItemGroup.COMBAT)));

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
