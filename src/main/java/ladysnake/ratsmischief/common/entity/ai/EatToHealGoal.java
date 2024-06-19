package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.UseAction;

import java.util.List;

public class EatToHealGoal extends Goal {
	public final RatEntity rat;
	public int eatingTicks;

	public EatToHealGoal(RatEntity rat) {
		this.rat = rat;
	}

	@Override
	public boolean canStart() {
		return ((this.rat.isBreedingItem(this.rat.getMainHandStack()) && this.rat.getHealth() < this.rat.getMaxHealth()) || (this.rat.getMainHandStack().getItem() instanceof PotionItem && !(this.rat.getMainHandStack().getItem() instanceof ThrowablePotionItem))) && !this.rat.getMoveControl().isMoving();
	}

	@Override
	public void tick() {
		if (!this.rat.getWorld().isClient() && this.rat.isEating()) {
			if (!this.rat.getMoveControl().isMoving()) {
				super.tick();

				this.eatingTicks--;

				if (this.eatingTicks % 4 == 0 && this.eatingTicks > 0) {
					if (this.rat.getMainHandStack().getItem().getUseAction(this.rat.getMainHandStack()) == UseAction.DRINK) {
						this.rat.getWorld().playSoundFromEntity(null, this.rat, this.rat.getMainHandStack().getItem().getDrinkSound(), SoundCategory.NEUTRAL, 0.01F, this.rat.getRandom().nextFloat() * 0.4F + 0.8F);
					} else if (this.rat.getMainHandStack().getItem().getUseAction(this.rat.getMainHandStack()) == UseAction.EAT) {
						((ServerWorld) this.rat.getWorld()).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.rat.getMainHandStack()), this.rat.getX() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getY() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getZ() + this.rat.getRandom().nextGaussian() / 20f, 5, this.rat.getRandom().nextGaussian() / 20f, 0.2D + this.rat.getRandom().nextGaussian() / 20f, this.rat.getRandom().nextGaussian() / 20f, 0.025f);
						this.rat.getWorld().playSoundFromEntity(null, this.rat, this.rat.getMainHandStack().getItem().getEatSound(), SoundCategory.NEUTRAL, 0.01F, this.rat.getRandom().nextFloat() * 0.4F + 0.8F);
					}
				}

				if (this.eatingTicks <= 0) {
					if (this.rat.getMainHandStack().getItem() instanceof PotionItem) {
						ItemStack stack = this.rat.getMainHandStack();

						if (!this.rat.getWorld().isClient()) {
							List<StatusEffectInstance> list = PotionUtil.getPotionEffects(stack);

							for (StatusEffectInstance statusEffectInstance : list) {
								if (statusEffectInstance.getEffectType().isInstant()) {
									statusEffectInstance.getEffectType().applyInstantEffect(this.rat, this.rat, this.rat, statusEffectInstance.getAmplifier(), 1.0D);
								} else {
									this.rat.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
								}
							}
						}

						stack.decrement(1);
						this.rat.dropStack(new ItemStack(Items.GLASS_BOTTLE));
					} else if (this.rat.getMainHandStack().getItem().isFood()) {
						if (this.rat.getMainHandStack().getUseAction() == UseAction.DRINK) {
							this.rat.dropStack(new ItemStack(Items.GLASS_BOTTLE));
						}
						this.rat.heal((float) this.rat.getMainHandStack().getItem().getFoodComponent().getHunger());
						this.rat.getMainHandStack().getItem().getFoodComponent().getStatusEffects().forEach(statusEffectInstanceFloatPair -> {
							this.rat.addStatusEffect(statusEffectInstanceFloatPair.getFirst());
						});
						this.rat.getMainHandStack().getItem().finishUsing(this.rat.getMainHandStack(), this.rat.getWorld(), this.rat);
					}

					this.rat.setEating(false);
				}
			} else {
				this.rat.setEating(false);
			}
		}
	}

	@Override
	public void start() {
		this.rat.setEating(true);
		this.eatingTicks = 30;
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue() && this.rat.isEating();
	}
}
