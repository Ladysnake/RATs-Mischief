package ladysnake.ratsmischief.common;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import ladysnake.ratsmischief.client.render.item.recipe.SpyRatCraftingRecipe;
import ladysnake.ratsmischief.common.cca.KORatsScoreboardComponent;
import ladysnake.ratsmischief.common.cca.RatHornsComponent;
import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.init.*;
import ladysnake.ratsmischief.common.world.RatSpawner;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import org.jetbrains.annotations.NotNull;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldLoadEvents;
import org.quiltmc.qsl.lifecycle.api.event.ServerWorldTickEvents;
import software.bernie.geckolib3.GeckoLib;

public class RatsMischief implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
	public static final String MOD_ID = "ratsmischief";
	public static final SpecialRecipeSerializer<SpyRatCraftingRecipe> SPY_RAT_RECIPE = RecipeSerializer.register(
		"ratsmischief:crafting_special_spy_rat", new SpecialRecipeSerializer<>(SpyRatCraftingRecipe::new)
	);
	public static final Identifier ANCIENT_CITY_CHESTS = new Identifier("minecraft", "chests/ancient_city");

	public static RatEntity DEFAULT_RAT;

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize(ModContainer mod) {
		GeckoLib.initialize();

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
		Registry.register(Registry.PAINTING_VARIANT, new Identifier(MOD_ID, "a_rat_in_time"), new PaintingVariant(64, 48));

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
//					ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
//					EnchantedBookItem.addEnchantment(book, new EnchantmentLevelEntry(ModEnchantments.RAT_CURSE, 1));
//					LootPool lootPool = LootPool.builder()
//						.rolls(lootTableRange)
//						.conditionally(RandomChanceLootCondition.builder(0.3f).build())
//						.with(ItemEntry.builder(ModItems.CLOTHED_INGOT).build()).build();
//					supplier.pool(lootPool);
				}
			}
		});

		// init default rat
		ServerWorldLoadEvents.LOAD.register((server, world) -> {
			DEFAULT_RAT = ModEntities.RAT.create(world);
			DEFAULT_RAT.setRatType(RatEntity.Type.WILD);
		});
	}

	@Override
	public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(PlayerEntity.class, RatHornsComponent.KEY).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(RatHornsComponent::new);
	}

	@Override
	public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
		registry.registerScoreboardComponent(KORatsScoreboardComponent.KEY, KORatsScoreboardComponent::new);
	}
}
