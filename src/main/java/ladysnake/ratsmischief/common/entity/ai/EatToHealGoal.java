package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.ThrowablePotionItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;

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
					if ((Object) this.rat.getMainHandStack().get(DataComponentTypes.CONSUMABLE) instanceof ConsumableComponent consumable) {
						consumable.spawnParticlesAndPlaySound(this.rat.getRandom(), this.rat, this.rat.getMainHandStack(), 10);
					}
				}

				if (this.eatingTicks <= 0) {
					if ((Object) this.rat.getMainHandStack().get(DataComponentTypes.CONSUMABLE) instanceof ConsumableComponent consumable) {
						ItemStack stack = this.rat.getMainHandStack();

						if (!this.rat.getWorld().isClient()) {
							ItemStack result = consumable.finishConsumption(this.rat.getWorld(), this.rat, stack);
							this.rat.dropStack(result);
						}

						stack.decrement(1);
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
