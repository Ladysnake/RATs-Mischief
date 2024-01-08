package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BreedGoal extends Goal {
	protected final RatEntity rat;
	protected AnimalEntity target;

	public BreedGoal(RatEntity rat) {
		this.rat = rat;
	}

	@Override
	public boolean canStart() {
		return !this.rat.getMainHandStack().isEmpty() && !this.rat.getWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack())).isEmpty();
	}

	@Override
	public void start() {
		if (!this.rat.getMainHandStack().isEmpty() && !this.rat.getWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> !this.rat.getWorld().isClient() && animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack())).isEmpty()) {
			List<AnimalEntity> animalList = this.rat.getWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack()));
			AnimalEntity closestAnimal = animalList.get(0);
			for (AnimalEntity animalEntity : animalList) {
				if (animalEntity.squaredDistanceTo(this.rat) < closestAnimal.squaredDistanceTo(this.rat)) {
					closestAnimal = animalEntity;
				}
			}

			this.target = closestAnimal;
		}
	}

	@Override
	public void tick() {
		ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

		if (this.rat.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ()) <= 5) {
			if (this.rat.getOwner() instanceof PlayerEntity) {
				itemStack.decrement(1);
				this.target.lovePlayer((PlayerEntity) this.rat.getOwner());
			}

			this.target = null;
		} else {
			this.rat.getNavigation().startMovingTo(this.target, 1D);
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.target != null && !this.rat.getMainHandStack().isEmpty() && this.target.getBreedingAge() == 0 && this.target.canEat() && this.target.isBreedingItem(this.rat.getMainHandStack());
	}
}
