package ladysnake.ratsmischief.client.render.item.recipe;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.init.ModDataComponents;
import ladysnake.ratsmischief.common.init.ModItems;
import ladysnake.ratsmischief.common.item.RatItem;
import ladysnake.ratsmischief.common.util.RatData;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpyRatCraftingRecipe extends SpecialCraftingRecipe {
	private static final Ingredient RAT = Ingredient.ofItems(ModItems.RAT);
	private static final Ingredient RABBIT_HIDE = Ingredient.ofItems(Items.RABBIT_HIDE);
	private static final Ingredient ENDER_EYE = Ingredient.ofItems(Items.ENDER_EYE);

	public SpyRatCraftingRecipe(CraftingRecipeCategory category) {
		super(category);
	}


	@Override
	public boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Override
	public boolean matches(CraftingRecipeInput inventory, World world) {
		for (int i = 0; i < 3; ++i) {
			ItemStack itemStack = inventory.getStackInSlot(i);
			if (!itemStack.isEmpty()) {
				if (ENDER_EYE.test(itemStack)) {
					return RABBIT_HIDE.test(inventory.getStackInSlot(i + 3)) && RAT.test(inventory.getStackInSlot(i + 6));
				}
			}
		}

		return false;
	}
	@Override
	public ItemStack craft(CraftingRecipeInput inventory, RegistryWrapper.WrapperLookup registryManager) {
		ItemStack spyRatStack = ItemStack.EMPTY;

		for (int i = 6; i < 9; ++i) {
			ItemStack ratStack = inventory.getStackInSlot(i);
			if (!ratStack.isEmpty()) {
				if (ratStack.isOf(ModItems.RAT)
					&& ratStack.contains(ModDataComponents.RAT_ENTITY_DATA)
					&& ratStack.get(ModDataComponents.RAT_ENTITY_DATA).ratTag().getInt("Age").map(age -> age >= 0).orElse(false)) {
					if (inventory.getStackInSlot(i - 3).isOf(Items.RABBIT_HIDE) && inventory.getStackInSlot(i - 6).isOf(Items.ENDER_EYE)) {
						spyRatStack = ratStack.copy();
						NbtCompound ratNbt = RatItem.getRatTag(spyRatStack);
						ratNbt.putBoolean("Spy", true);
						ratNbt.putBoolean("ShouldReturnToOwnerInventory", false);
						spyRatStack.set(ModDataComponents.RAT_ENTITY_DATA, new RatData(ratNbt));
					}
				}
			}
		}

		return !spyRatStack.isEmpty() ? spyRatStack : ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
		return RatsMischief.SPY_RAT_RECIPE;
	}
}
