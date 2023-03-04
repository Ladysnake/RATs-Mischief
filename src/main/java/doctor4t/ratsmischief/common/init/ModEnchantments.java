package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.enchantments.MischiefCurseEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModEnchantments {
	Map<Enchantment, Identifier> ENCHANTMENTS = new LinkedHashMap<>();

	Enchantment MISCHIEF_CURSE = createEnchantment("mischief_curse", new MischiefCurseEnchantment(Enchantment.Rarity.VERY_RARE, EquipmentSlot.values()));

	@SuppressWarnings("SameParameterValue")
	private static Enchantment createEnchantment(String name, Enchantment entity) {
		ENCHANTMENTS.put(entity, new Identifier(RatsMischief.MOD_ID, name));
		return entity;
	}

	static void initialize() {
		ENCHANTMENTS.keySet().forEach(entityType -> Registry.register(Registry.ENCHANTMENT, ENCHANTMENTS.get(entityType), entityType));
	}
}
