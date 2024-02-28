package ladysnake.ratsmischief.common.init;//package doctor4t.ratsmischief.common.init;
//
//import doctor4t.ratsmischief.common.RatsMischief;
//import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
//import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//import net.minecraft.item.Item;
//import net.minecraft.item.ItemGroup;
//import net.minecraft.item.ItemStack;
//import net.minecraft.item.Items;
//import net.minecraft.text.Text;
//import net.minecraft.util.Identifier;
//
//public class ModItemGroup {
//	public static ItemGroup MOD_ITEMS;
//
//	public static void initialize() {
//		MOD_ITEMS = FabricItemGroup.builder(RatsMischief.id("items"))
//				.name(Text.literal("Explosive Ideas"))
//				.icon(() -> new ItemStack(Items.TNT))
//				.build();
//	}
//
//	public static void addToItemGroup(ItemGroup group, Item item) {
//		ItemGroupEvents.modifyEntriesEvent(group).register(entries -> entries.addItem(item));
//	}
//}
