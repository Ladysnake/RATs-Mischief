package doctor4t.ratsmischief.common.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class RattyArmorItem extends ArmorItem {
	public RattyArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static RattyArmorType getType(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		return RattyArmorType.values()[clampLoop(compound.getInt("type"), 0, RattyArmorType.values().length - 1)];
	}

	public static void incrementType(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("offset", clampLoop(compound.getInt("type") + 1, 0, RattyArmorType.values().length - 1));
	}

	public static int clampLoop(int input, int start, int end) {
		if (start - end == 0) {
			return start;
		}
		if (end < start) {
			int temp = start;
			start = end;
			end = temp;
		}
		return input < start ? end - (start - input) % (end - start) : start + (input - start) % (end - start);
	}

	enum RattyArmorType {
		NONE,
		RESISTANCE,
		DAMAGE,
		MINING_SPEED
	}

	public static class RattyArmorMaterial implements ArmorMaterial {
		public static final RattyArmorMaterial INSTANCE = new RattyArmorMaterial();
		private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
		private static final int[] PROTECTION_AMOUNTS = new int[]{1, 4, 5, 2};

		@Override
		public int getDurability(EquipmentSlot slot) {
			return BASE_DURABILITY[slot.getEntitySlotId()] * 22;
		}

		@Override
		public int getProtectionAmount(EquipmentSlot slot) {
			return PROTECTION_AMOUNTS[slot.getEntitySlotId()];
		}

		@Override
		public int getEnchantability() {
			return 16;
		}

		@Override
		public SoundEvent getEquipSound() {
			return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
		}

		@Override
		public Ingredient getRepairIngredient() {
			return Ingredient.ofItems(Items.LEATHER);
		}

		@Override
		public String getName() {
			return "ratty";
		}

		@Override
		public float getToughness() {
			return 0;
		}

		@Override
		public float getKnockbackResistance() {
			return 0;
		}
	}
}
