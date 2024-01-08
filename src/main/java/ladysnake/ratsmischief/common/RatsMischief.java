package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.client.render.item.recipe.SpyRatCraftingRecipe;
import ladysnake.ratsmischief.common.init.ModBlocks;
import ladysnake.ratsmischief.common.init.ModEnchantments;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModLootTables;
import ladysnake.ratsmischief.common.init.ModSoundEvents;
import ladysnake.ratsmischief.common.init.ModStatusEffects;
import ladysnake.ratsmischief.common.world.RatSpawner;
import ladysnake.ratsmischief.mialeemisc.MialeeMisc;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents;

public class RatsMischief implements ModInitializer {
	public static final String MOD_ID = "ratsmischief";
	public static final SpecialRecipeSerializer<SpyRatCraftingRecipe> SPY_RAT_RECIPE = RecipeSerializer.register(
		"ratsmischief:crafting_special_spy_rat", new SpecialRecipeSerializer<>(SpyRatCraftingRecipe::new)
	);
	public static final Identifier ANCIENT_CITY_CHESTS = new Identifier("minecraft", "chests/ancient_city");

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		// initializing stuff
		ModEntities.initialize();
		ModBlocks.initialize();
//		ModItemGroup.initialize();
		ModItems.initialize();
		ModSoundEvents.initialize();
		ModEnchantments.initialize();
		ModLootTables.initialize();
		ModStatusEffects.initialize();

		// custom rat spawner in abandoned villages
		RatSpawner ratSpawner = new RatSpawner();
		ServerWorldTickEvents.END.register((server, world) -> {
			// spawn rats
			ratSpawner.spawn(world, world.getDifficulty() != Difficulty.PEACEFUL, server.shouldSpawnAnimals());
		});

		// rat kid painting
		Registry.register(Registries.PAINTING_VARIANT, new Identifier(MOD_ID, "a_rat_in_time"), new PaintingVariant(64, 48));

		// clothed ingots and rat curse books spawning in ancient city chests
		UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
		LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
			if (ANCIENT_CITY_CHESTS.equals(id)) {
				{ // clothed ingot
					LootPool lootPool = LootPool.builder()
						.rolls(lootTableRange)
						.conditionally(RandomChanceLootCondition.builder(0.3f).build())
						.with(ItemEntry.builder(ModItems.CLOTHED_INGOT).build()).build();
					supplier.pool(lootPool);
				}
				{ // rat curse book
					ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
					EnchantedBookItem.addEnchantment(book, new EnchantmentLevelEntry(ModEnchantments.RAT_CURSE, 1));
					LootPool lootPool = LootPool.builder()
						.rolls(lootTableRange)
						.conditionally(RandomChanceLootCondition.builder(0.3f).build())
						.with(ItemEntry.builder(ModItems.CLOTHED_INGOT).build()).build();
					supplier.pool(lootPool);
				}
			}
		});

		MialeeMisc.onInitialize();
	}
}
