package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.item.*;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.qsl.item.setting.api.QuiltItemSettings;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public interface ModItems {
	Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

	ItemGroup GROUP = FabricItemGroupBuilder.create(new Identifier(RatsMischief.MOD_ID, "rats_mischief"))
		.icon(ModItems::getIconStack)
		.build();

	static @NotNull ItemStack getIconStack() {
		ItemStack iconStack = new ItemStack(ModItems.RAT_MASTER_POUCH);
		iconStack.getOrCreateSubNbt(RatsMischief.MOD_ID).putFloat("filled", 1f);
		return iconStack;
	}

	//	Item MOD_ITEM = createItem("mod_item", new ModItem(new QuiltItemSettings()));
	Item RAT = createItem("rat", new RatItem(new QuiltItemSettings().maxCount(1)));
	Item RAT_SPAWN_EGG = createItem("rat_spawn_egg", new SpawnEggItem(ModEntities.RAT, 0x2E1C1C, 0x241317, new QuiltItemSettings().group(GROUP)));

	Item LEATHER_RAT_POUCH = createItem("leather_rat_pouch", new RatPouchItem(new QuiltItemSettings().group(GROUP).maxCount(1), 5));
	Item TWISTED_RAT_POUCH = createItem("twisted_rat_pouch", new RatPouchItem(new QuiltItemSettings().group(GROUP).maxCount(1), 10));
	Item PURPUR_RAT_POUCH = createItem("purpur_rat_pouch", new RatPouchItem(new QuiltItemSettings().group(GROUP).maxCount(1), 15));
	Item RAT_MASTER_POUCH = createItem("rat_master_pouch", new RatPouchItem(new QuiltItemSettings().group(GROUP).maxCount(1), 20));

	Item CLOTHED_INGOT = createItem("clothed_ingot", new Item(new QuiltItemSettings().group(GROUP)));
	Item RAT_MASTER_HOOD = createItem("rat_master_hood", new RatHoodItem(RatArmorItem.RatMasterArmorMaterial.INSTANCE, EquipmentSlot.HEAD, new QuiltItemSettings().group(GROUP)));
	Item RAT_MASTER_CLOAK = createItem("rat_master_cloak", new RatCloakItem(RatArmorItem.RatMasterArmorMaterial.INSTANCE, EquipmentSlot.CHEST, new QuiltItemSettings().group(GROUP)));
	Item RAT_MASTER_BREECHES = createItem("rat_master_breeches", new RatArmorItem(RatArmorItem.RatMasterArmorMaterial.INSTANCE, EquipmentSlot.LEGS, new QuiltItemSettings().group(GROUP)));
	Item RAT_MASTER_GREAVES = createItem("rat_master_greaves", new RatArmorItem(RatArmorItem.RatMasterArmorMaterial.INSTANCE, EquipmentSlot.FEET, new QuiltItemSettings().group(GROUP)));
	Item RAT_MASTER_MIRROR = createItem("rat_master_mirror", new RatMasterMirrorItem(new QuiltItemSettings().group(GROUP).maxCount(1)));
	Item RAT_MASTER_OCARINA = createItem("rat_master_ocarina", new RatMasterOcarinaItem(new QuiltItemSettings().group(GROUP).maxCount(1)));
	Item RAT_MASTER_MASK = createItem("rat_master_mask", new RatMaskItem(new QuiltItemSettings().group(GROUP).maxCount(1)));

	Item RAT_BELLICIST_HOOD = createItem("rat_bellicist_hood", new RatHoodItem(RatArmorItem.RatBellicistArmorMaterial.INSTANCE, EquipmentSlot.HEAD, new QuiltItemSettings().group(GROUP)));
	Item RAT_BELLICIST_CLOAK = createItem("rat_bellicist_cloak", new RatCloakItem(RatArmorItem.RatBellicistArmorMaterial.INSTANCE, EquipmentSlot.CHEST, new QuiltItemSettings().group(GROUP)));
	Item RAT_BELLICIST_BREECHES = createItem("rat_bellicist_breeches", new RatArmorItem(RatArmorItem.RatBellicistArmorMaterial.INSTANCE, EquipmentSlot.LEGS, new QuiltItemSettings().group(GROUP)));
	Item RAT_BELLICIST_GREAVES = createItem("rat_bellicist_greaves", new RatArmorItem(RatArmorItem.RatBellicistArmorMaterial.INSTANCE, EquipmentSlot.FEET, new QuiltItemSettings().group(GROUP)));
	Item RAT_BELLICIST_HORNS = createItem("rat_bellicist_horns", new RatHornsItem(new QuiltItemSettings().group(GROUP).maxCount(1)));
	Item RAT_BELLICIST_CARNYX = createItem("rat_bellicist_carnyx", new RatCarnyxItem(new QuiltItemSettings().group(GROUP).maxCount(1)));


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
