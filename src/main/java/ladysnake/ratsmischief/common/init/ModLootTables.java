package ladysnake.ratsmischief.common.init;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;

public class ModLootTables {
	public static final float RAT_CURSE_CHANCE = 0.2f; // 20%

	public static void initialize() {
		LootTableEvents.MODIFY.register((resourceManager, lootManager, identifier, fabricLootSupplierBuilder, lootTableSetter) -> {
			if (LootTables.ANCIENT_CITY_CHEST.equals(identifier)) {
				fabricLootSupplierBuilder.pool(new LootPool.Builder()
					.rolls(ConstantLootNumberProvider.create(1))
					.with(ItemEntry.builder(Items.BOOK).apply(() -> new LootFunction() {
						@Override
						public LootFunctionType getType() {
							throw new UnsupportedOperationException();
						}

						@Override
						public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
							EnchantmentLevelEntry enchantment = new EnchantmentLevelEntry(ModEnchantments.RAT_CURSE, 1);
							return EnchantedBookItem.forEnchantment(enchantment);
						}
					}).build())
					.conditionally(RandomChanceLootCondition.builder(RAT_CURSE_CHANCE).build())
					.build()
				);
			}
		});
	}
}
