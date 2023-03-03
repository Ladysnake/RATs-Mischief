package doctor4t.ratsmischief.common.item;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialeemisc.util.MialeeMath;

import java.util.List;
import java.util.Set;

public class MasterRatArmorItem extends ArmorItem {
	public static final Set<EquipmentSlot> SLOTS = Set.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);

	public MasterRatArmorItem(ArmorMaterial material, EquipmentSlot slot, Settings settings) {
		super(material, slot, settings);
	}

	public static MasterArmorBoost getType(ItemStack stack) {
		NbtCompound compound = stack.getOrCreateNbt();
		return MasterArmorBoost.values()[MialeeMath.clampLoop(compound.getInt("type"), 0, MasterArmorBoost.values().length)];
	}

	public static float getResistanceMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : MasterRatArmorItem.SLOTS) {
				if (MasterRatArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.RESISTANCE) {
					bonus += 0.15f;
				}
			}
			return 1.0f - bonus;
		}
		return 1f;
	}

	public static float getDamageMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : MasterRatArmorItem.SLOTS) {
				if (MasterRatArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.DAMAGE) {
					bonus += 0.25f;
				}
			}
			return 1.0f + bonus;
		}
		return 1f;
	}

	public static float getMiningSpeedMultiplier(LivingEntity owner) {
		if (owner != null) {
			float bonus = 0.0f;
			for (EquipmentSlot slot : MasterRatArmorItem.SLOTS) {
				if (MasterRatArmorItem.getType(owner.getEquippedStack(slot)) == MasterArmorBoost.MINING_SPEED) {
					bonus += 0.25f;
				}
			}
			return 1.0f + bonus;
		}
		return 1f;
	}

	public void incrementType(ItemStack stack, boolean sneaking) {
		NbtCompound compound = stack.getOrCreateNbt();
		compound.putInt("type", MialeeMath.clampLoop(compound.getInt("type") + 1, 1, MasterArmorBoost.values().length));
	}

	@Override
	public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
		tooltip.add(Text.translatable("item.ratsmischief.rat_master_armor.tooltip." + getType(stack).name().toLowerCase()).formatted(Formatting.GRAY));
		super.appendTooltip(stack, world, tooltip, context);
	}

	public enum MasterArmorBoost {
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
