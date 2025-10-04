package ladysnake.ratsmischief.common;

import ladysnake.ratsmischief.client.render.item.recipe.SpyRatCraftingRecipe;
import ladysnake.ratsmischief.common.init.*;
import ladysnake.ratsmischief.common.world.RatSpawner;
import ladysnake.ratsmischief.mialeemisc.MialeeMisc;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;

public class RatsMischief implements ModInitializer {
	public static final String MOD_ID = "ratsmischief";
	public static final SpecialCraftingRecipe.SpecialRecipeSerializer<SpyRatCraftingRecipe> SPY_RAT_RECIPE = Registry.register(Registries.RECIPE_SERIALIZER, id("crafting_special_spy_rat"), new SpecialCraftingRecipe.SpecialRecipeSerializer<>(SpyRatCraftingRecipe::new));

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		// initializing stuff
		ModEntities.initialize();
//		ModBlocks.initialize();
//		ModItemGroup.initialize();
		ModDataComponents.initialize();
		ModItems.initialize();
		ModLootTables.initialize();
		ModSoundEvents.initialize();
		ModStatusEffects.initialize();
		ModTags.initialize();

		// custom rat spawner in abandoned villages
		RatSpawner ratSpawner = new RatSpawner();
		ServerTickEvents.END_WORLD_TICK.register(world -> {
			// spawn rats
			ratSpawner.spawn(world, world.getDifficulty() != Difficulty.PEACEFUL, world.getServer().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING));
		});


		MialeeMisc.onInitialize();
	}
}
