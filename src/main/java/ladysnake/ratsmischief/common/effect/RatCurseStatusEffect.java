package ladysnake.ratsmischief.common.effect;

import ladysnake.ratsmischief.common.init.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class RatCurseStatusEffect extends StatusEffect {
	public RatCurseStatusEffect(StatusEffectType type, int color) {
		super(type, color);
	}

	@Override
	public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		super.onApplied(entity, attributes, amplifier);

		if (entity instanceof PlayerEntity player) {
			for (int i = 0; i < 5; i++) {
				((ServerWorld) player.world).spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + player.getHeight() / 2f, player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
				((ServerWorld) player.world).spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
			}

			player.playSound(SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.PLAYERS, 1.0f, 1.0f);
			//RemnantComponent.get(player).become(RatsMischiefRequiemPlugin.RATIFIED_REMNANT_TYPE);
		}
	}

	@Override
	public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
		super.onRemoved(entity, attributes, amplifier);

		if (entity instanceof PlayerEntity player) {
			for (int i = 0; i < 5; i++) {
				((ServerWorld) player.world).spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + player.getHeight() / 2f, player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
				((ServerWorld) player.world).spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
			}

			player.playSound(SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
			player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.RAT_CURSE_COOLDOWN, 1200, 0, false, false, true));
			//RemnantComponent.get(player).become(RemnantTypes.MORTAL);
		}
	}
}
