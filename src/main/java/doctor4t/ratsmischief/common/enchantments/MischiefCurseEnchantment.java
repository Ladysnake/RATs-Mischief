package doctor4t.ratsmischief.common.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;

public class MischiefCurseEnchantment extends Enchantment {
	public MischiefCurseEnchantment(Enchantment.Rarity weight, EquipmentSlot... slotTypes) {
		super(weight, EnchantmentTarget.WEARABLE, slotTypes);
	}

	@Override
	public int getMinPower(int level) {
		return 25;
	}

	@Override
	public int getMaxPower(int level) {
		return 50;
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public boolean isTreasure() {
		return true;
	}

	@Override
	public boolean isCursed() {
		return true;
	}
}
