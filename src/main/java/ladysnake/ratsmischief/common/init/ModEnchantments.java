package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface ModEnchantments {
	RegistryKey<Enchantment> RAT_CURSE = RegistryKey.of(RegistryKeys.ENCHANTMENT, RatsMischief.id("rat_curse"));
}
