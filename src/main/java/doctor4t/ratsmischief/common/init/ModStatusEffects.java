package doctor4t.ratsmischief.common.init;

import doctor4t.ratsmischief.common.RatsMischief;
import doctor4t.ratsmischief.common.effect.RatCurseStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ModStatusEffects {
	Map<StatusEffect, Identifier> EFFECTS = new LinkedHashMap<>();

	StatusEffect RAT_CURSE = createStatusEffect("rat_curse", new RatCurseStatusEffect(StatusEffectType.NEUTRAL, 0x35A2F3));

	private static <T extends StatusEffect> T createStatusEffect(String name, T effect) {
		EFFECTS.put(effect, new Identifier(RatsMischief.MOD_ID, name));
		return effect;
	}

	static void initialize() {
		EFFECTS.keySet().forEach(item -> Registry.register(Registry.STATUS_EFFECT, EFFECTS.get(item), item));
	}
}
