package ladysnake.ratsmischief.common.init;

import ladysnake.ratsmischief.common.RatsMischief;
import ladysnake.ratsmischief.common.effect.RatCurseCooldownStatusEffect;
import ladysnake.ratsmischief.common.effect.RatCurseStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModStatusEffects {
	Map<StatusEffect, Identifier> EFFECTS = new LinkedHashMap<>();

	StatusEffect RAT_CURSE = createStatusEffect("rat_curse", new RatCurseStatusEffect(StatusEffectType.NEUTRAL, 0x31363F));
	StatusEffect RAT_CURSE_COOLDOWN = createStatusEffect("rat_curse_cooldown", new RatCurseCooldownStatusEffect(StatusEffectType.NEUTRAL, 0x31363F));

	private static <T extends StatusEffect> T createStatusEffect(String name, T effect) {
		EFFECTS.put(effect, new Identifier(RatsMischief.MOD_ID, name));
		return effect;
	}

	static void initialize() {
		EFFECTS.keySet().forEach(item -> Registry.register(Registries.STATUS_EFFECT, EFFECTS.get(item), item));
	}
}
