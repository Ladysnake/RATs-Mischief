package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.enchantments.RatCurseEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
	Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap<>();

	Enchantment RAT_CURSE = createEnchantment("rat_curse", new RatCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.values()));

	@SuppressWarnings("SameParameterValue")
	private static Enchantment createEnchantment(String name, Enchantment entity) {
		ENCHANTMENTS.put(entity, RatsMischief.id(name));
		return entity;
	}

	static void initialize() {
		ENCHANTMENTS.keySet().forEach(entityType -> Registry.register(Registries.ENCHANTMENT, ENCHANTMENTS.get(entityType), entityType));
	}
}
