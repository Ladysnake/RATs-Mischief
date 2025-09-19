package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.effect.RatCurseCooldownStatusEffect;
import ladysnake.ratsmischief.common.effect.RatCurseStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModStatusEffects {
	//Had to rewrite the registrator to provide registry entries

	RegistryEntry<StatusEffect> RAT_CURSE = createStatusEffect("rat_curse", new RatCurseStatusEffect(StatusEffectCategory.NEUTRAL, 0x31363F));
	RegistryEntry<StatusEffect> RAT_CURSE_COOLDOWN = createStatusEffect("rat_curse_cooldown", new RatCurseCooldownStatusEffect(StatusEffectCategory.NEUTRAL, 0x31363F));

	private static <T extends StatusEffect> RegistryEntry<StatusEffect> createStatusEffect(String name, T effect) {
		return Registry.registerReference(Registries.STATUS_EFFECT, RatsMischief.id(name), effect);
	}

	static void initialize() {

	}
}
