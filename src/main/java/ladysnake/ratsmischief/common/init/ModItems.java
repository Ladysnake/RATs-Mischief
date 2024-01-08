package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.RatItem;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
import ladysnake.ratsmischief.common.item.RatMasterCloakItem;
import ladysnake.ratsmischief.common.item.RatMasterHoodItem;
import ladysnake.ratsmischief.common.item.RatMasterMaskItem;
import ladysnake.ratsmischief.common.item.RatMasterMirrorItem;
import ladysnake.ratsmischief.common.item.RatMasterOcarinaItem;
import ladysnake.ratsmischief.common.item.RatPouchItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	//	Item MOD_ITEM = createItem("mod_item", new ModItem(new QuiltItemSettings()));
	Item RAT = createItem("rat", new RatItem(new QuiltItemSettings().maxCount(1)));
	Item RAT_SPAWN_EGG = createItem("rat_spawn_egg", new SpawnEggItem(ModEntities.RAT, 0x2E1C1C, 0x241317, new QuiltItemSettings()));

	Item LEATHER_RAT_POUCH = createItem("leather_rat_pouch", new RatPouchItem(new QuiltItemSettings().maxCount(1), 5));
	Item TWISTED_RAT_POUCH = createItem("twisted_rat_pouch", new RatPouchItem(new QuiltItemSettings().maxCount(1), 10));
	Item PURPUR_RAT_POUCH = createItem("purpur_rat_pouch", new RatPouchItem(new QuiltItemSettings().maxCount(1), 15));
	Item RAT_MASTER_POUCH = createItem("rat_master_pouch", new RatPouchItem(new QuiltItemSettings().maxCount(1), 20));

	Item CLOTHED_INGOT = createItem("clothed_ingot", new Item(new QuiltItemSettings()));
	Item RAT_MASTER_HOOD = createItem("rat_master_hood", new RatMasterHoodItem(RatMasterArmorItem.RatMasterArmorMaterial.INSTANCE, ArmorItem.ArmorSlot.HELMET, new QuiltItemSettings()));
	Item RAT_MASTER_CLOAK = createItem("rat_master_cloak", new RatMasterCloakItem(RatMasterArmorItem.RatMasterArmorMaterial.INSTANCE, ArmorItem.ArmorSlot.CHESTPLATE, new QuiltItemSettings()));
	Item RAT_MASTER_BREECHES = createItem("rat_master_breeches", new RatMasterArmorItem(RatMasterArmorItem.RatMasterArmorMaterial.INSTANCE, ArmorItem.ArmorSlot.LEGGINGS, new QuiltItemSettings()));
	Item RAT_MASTER_GREAVES = createItem("rat_master_greaves", new RatMasterArmorItem(RatMasterArmorItem.RatMasterArmorMaterial.INSTANCE, ArmorItem.ArmorSlot.BOOTS, new QuiltItemSettings()));
	Item RAT_MASTER_MIRROR = createItem("rat_master_mirror", new RatMasterMirrorItem(new QuiltItemSettings().maxCount(1)));
	Item RAT_MASTER_OCARINA = createItem("rat_master_ocarina", new RatMasterOcarinaItem(new QuiltItemSettings().maxCount(1)));
	Item RAT_MASTER_MASK = createItem("rat_master_mask", new RatMasterMaskItem(new QuiltItemSettings().maxCount(1)));

	private static <T extends Item> T createItem(String name, T item) {
		ITEMS.put(item, new Identifier(RatsMischief.MOD_ID, name));
		return item;
	}

	static void initialize() {
		ITEMS.keySet().forEach(item -> {
			Registry.register(Registries.ITEM, ITEMS.get(item), item);
//			ModItemGroup.addToItemGroup(ModItemGroup.MOD_ITEMS, item);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> {
			entries.addItem(RAT_MASTER_HOOD);
			entries.addItem(RAT_MASTER_CLOAK);
			entries.addItem(RAT_MASTER_BREECHES);
			entries.addItem(RAT_MASTER_GREAVES);
			entries.addItem(RAT_MASTER_MASK);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
			entries.addItem(CLOTHED_INGOT);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(entries -> {
			entries.addItem(RAT_SPAWN_EGG);
		});
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS_AND_UTILITIES).register(entries -> {
			entries.addItem(LEATHER_RAT_POUCH);
			entries.addItem(TWISTED_RAT_POUCH);
			entries.addItem(PURPUR_RAT_POUCH);
			entries.addItem(RAT_MASTER_POUCH);
			entries.addItem(RAT_MASTER_MIRROR);
			entries.addItem(RAT_MASTER_OCARINA);
		});
	}
}
