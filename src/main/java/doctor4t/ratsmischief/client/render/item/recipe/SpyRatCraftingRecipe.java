package doctor4t.ratsmischief.client.render.item.recipe;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.init.ModItems;
import doctor4t.ratsmischief.common.item.RatItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SpyRatCraftingRecipe extends SpecialCraftingRecipe {
	private static final Ingredient RAT = Ingredient.ofItems(ModItems.RAT);
	private static final Ingredient RABBIT_HIDE = Ingredient.ofItems(Items.RABBIT_HIDE);
	private static final Ingredient ENDER_EYE = Ingredient.ofItems(Items.ENDER_EYE);

	public SpyRatCraftingRecipe(Identifier id) {
		super(id);
	}

	@Override
	public boolean isIgnoredInRecipeBook() {
		return false;
	}

	@Override
	public boolean matches(CraftingInventory inventory, World world) {
		for (int i = 0; i < 3; ++i) {
			ItemStack itemStack = inventory.getStack(i);
			if (!itemStack.isEmpty()) {
				if (ENDER_EYE.test(itemStack)) {
					return RABBIT_HIDE.test(inventory.getStack(i + 3)) && RAT.test(inventory.getStack(i + 6));
				}
			}
		}

		return false;
	}

	@Override
	public ItemStack craft(CraftingInventory inventory) {
		ItemStack spyRatStack = ItemStack.EMPTY;

		for (int i = 6; i < 9; ++i) {
			ItemStack ratStack = inventory.getStack(i);
			if (!ratStack.isEmpty()) {
				if (ratStack.getItem() instanceof RatItem) {
					if (inventory.getStack(i - 3).isOf(Items.RABBIT_HIDE) && inventory.getStack(i - 6).isOf(Items.ENDER_EYE)) {
						spyRatStack = ratStack.copy();
						RatItem.getRatTag(spyRatStack).putBoolean("Spy", true);
						RatItem.getRatTag(spyRatStack).putBoolean("ShouldReturnToOwnerInventory", false);
					}
				}
			}
		}

		return !spyRatStack.isEmpty() ? spyRatStack : ItemStack.EMPTY;
	}

	public ItemStack getOutput() {
		return new ItemStack(ModItems.RAT);
	}

	@Override
	public boolean fits(int width, int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return RatsMischief.SPY_RAT_RECIPE;
	}
}
