package ladysnake.ratsmischief.common.init;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetEnchantmentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;

public class ModLootTables {
	private static final float CLOTHED_INGOT_CHANCE = 0.3f; // 30%
	private static final float RAT_CURSE_CHANCE = 0.2f; // 20%
	private static final LootNumberProvider CONST_1 = ConstantLootNumberProvider.create(1);

	public static void initialize() {
		// clothed ingots and rat curse books spawning in ancient city chests
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (LootTables.ANCIENT_CITY_CHEST.equals(id)) {
				{ // clothed ingot
					LootPool lootPool = LootPool.builder()
						.rolls(CONST_1)
						.conditionally(RandomChanceLootCondition.builder(CLOTHED_INGOT_CHANCE).build())
						.with(ItemEntry.builder(ModItems.CLOTHED_INGOT).build()).build();
					supplier.pool(lootPool);
				}
				{ // rat curse book
					LootPool lootPool = LootPool.builder()
						.rolls(CONST_1)
						.conditionally(RandomChanceLootCondition.builder(RAT_CURSE_CHANCE).build())
						.with(ItemEntry.builder(Items.ENCHANTED_BOOK).apply((new SetEnchantmentsLootFunction.Builder()).enchantment(ModEnchantments.RAT_CURSE, CONST_1)).build()).build();
					supplier.pool(lootPool);
				}
			}
		});
	}
}
