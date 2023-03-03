package doctor4t.ratsmischief.common;

import doctor4t.ratsmischief.common.init.ModBlocks;
import doctor4t.ratsmischief.common.init.ModEntities;
import doctor4t.ratsmischief.common.init.ModItems;
import doctor4t.ratsmischief.common.init.ModSoundEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import software.bernie.geckolib3.GeckoLib;

public class RatsMischief implements ModInitializer {
	public static final String MOD_ID = "ratsmischief";
	private static final Identifier ANCIENT_CITY_CHESTS = new Identifier("minecraft", "chests/ancient_city");

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLib.initialize();

		// initializing stuff
		ModEntities.initialize();
		ModBlocks.initialize();
//		ModItemGroup.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();

		// rat kid painting
		Registry.register(Registry.PAINTING_VARIANT, new Identifier(MOD_ID, "a_rat_in_time"), new PaintingVariant(64, 48));

		// clothed ingot spawning in ancient city chests
		UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
		LootCondition chanceLootCondition = RandomChanceLootCondition.builder(0.3f).build();
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (ANCIENT_CITY_CHESTS.equals(id)) {
				LootPool lootPool = LootPool.builder()
						.rolls(lootTableRange)
						.conditionally(chanceLootCondition)
						.with(ItemEntry.builder(ModItems.CLOTHED_INGOT).build()).build();
				supplier.pool(lootPool);
			}
		});
	}

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}
}
