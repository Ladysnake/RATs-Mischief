package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.client.render.item.recipe.SpyRatCraftingRecipe;
import ladysnake.ratsmischief.common.init.ModEnchantments;
import ladysnake.ratsmischief.common.init.ModEntities;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.init.ModLootTables;
import ladysnake.ratsmischief.common.init.ModSoundEvents;
import ladysnake.ratsmischief.common.init.ModStatusEffects;
import ladysnake.ratsmischief.common.init.ModTags;
import ladysnake.ratsmischief.common.world.RatSpawner;
import ladysnake.ratsmischief.mialeemisc.MialeeMisc;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;

public class RatsMischief implements ModInitializer {
	public static final String MOD_ID = "ratsmischief";
	public static final SpecialRecipeSerializer<SpyRatCraftingRecipe> SPY_RAT_RECIPE = Registry.register(Registries.RECIPE_SERIALIZER, id("crafting_special_spy_rat"), new SpecialRecipeSerializer<>(SpyRatCraftingRecipe::new));

	public static Identifier id(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// initializing stuff
		ModEntities.initialize();
//		ModBlocks.initialize();
//		ModItemGroup.initialize();
		ModItems.initialize();
		ModEnchantments.initialize();
		ModLootTables.initialize();
		ModSoundEvents.initialize();
		ModStatusEffects.initialize();
		ModTags.initialize();

		// custom rat spawner in abandoned villages
		RatSpawner ratSpawner = new RatSpawner();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			// spawn rats
			ratSpawner.spawn(world, world.getDifficulty() != Difficulty.PEACEFUL, world.getServer().shouldSpawnAnimals());
		});

		// rat kid painting
		Registry.register(Registries.PAINTING_VARIANT, RatsMischief.id("a_rat_in_time"), new PaintingVariant(64, 48));

		MialeeMisc.onInitialize();
	}
}
