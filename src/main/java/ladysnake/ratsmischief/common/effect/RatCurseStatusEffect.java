package ladysnake.ratsmischief.common.effect;

import ladysnake.ratsmischief.common.init.ModStatusEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class RatCurseStatusEffect extends StatusEffect {
	public RatCurseStatusEffect(StatusEffectCategory type, int color) {
		super(type, color);
	}


	@Override
	public void onApplied(LivingEntity entity, int amplifier) {
		super.onApplied(entity, amplifier);

		if (entity instanceof PlayerEntity player) {
			if (player instanceof ServerPlayerEntity serverPlayer) {
				for (int i = 0; i < 5; i++) {
					serverPlayer.getWorld().spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + player.getHeight() / 2f, player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
					serverPlayer.getWorld().spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
				}
			}

			player.playSound(SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE,  1.0f, 1.0f);
			//RemnantComponent.get(player).become(RatsMischiefRequiemPlugin.RATIFIED_REMNANT_TYPE);
		}
	}

	@Override
	public void onRemoved(AttributeContainer attributeContainer) {
	}
	//TODO Figure out how to deal with this
	/*@Override
	public void onRemoved(LivingEntity entity,  int amplifier) {
		super.onRemoved(entity, amplifier);

		if (entity instanceof PlayerEntity player) {
			if (player instanceof ServerPlayerEntity serverPlayer) {
				for (int i = 0; i < 5; i++) {
					serverPlayer.getWorld().spawnParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + player.getHeight() / 2f, player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
					serverPlayer.getWorld().spawnParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 100, 0.5f, 1f, 0.5f, 0.01f);
				}
			}

			player.playSound(SoundEvents.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1.0f, 1.0f);
			player.addStatusEffect(new StatusEffectInstance(ModStatusEffects.RAT_CURSE_COOLDOWN, 1200, 0, false, false, true));
			//RemnantComponent.get(player).become(RemnantTypes.MORTAL);
		}
	}*/
}
