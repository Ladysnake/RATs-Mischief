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
import xyz.amymialee.mialeemisc.util.MialeeMath;

public class MasterRatArmorItem extends ArmorItem {
	public MasterRatArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static RattyArmorType getType(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		return RattyArmorType.values()[MialeeMath.clampLoop(compound.getInt("type"), 0, RattyArmorType.values().length - 1)];
	}

	public static void incrementType(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("offset", MialeeMath.clampLoop(compound.getInt("type") + 1, 0, RattyArmorType.values().length - 1));
	}

	enum RattyArmorType {
		NONE,
		RESISTANCE,
		DAMAGE,
		MINING_SPEED
	}

	public static class MasterRatArmorMaterial implements ArmorMaterial {
		public static final MasterRatArmorMaterial INSTANCE = new MasterRatArmorMaterial();
		private static final int[] BASE_DURABILITY = new int[]{13, 15, 16, 11};
		private static final int[] PROTECTION_AMOUNTS = new int[]{3, 6, 8, 3};

		@Override
		public int getDurability(EquipmentSlot slot) {
			return BASE_DURABILITY[slot.getEntitySlotId()] * 38;
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
			return "masterrat";
		}

		@Override
		public float getToughness() {
			return 2.0f;
		}

		@Override
		public float getKnockbackResistance() {
			return 0.0f;
		}
	}
}
