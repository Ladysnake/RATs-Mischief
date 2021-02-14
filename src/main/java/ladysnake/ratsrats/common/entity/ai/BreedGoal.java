package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class BreedGoal extends Goal {
    protected final RatEntity rat;
    protected AnimalEntity target;

    public BreedGoal(RatEntity rat) {
        this.rat = rat;
    }

    @Override
    public boolean canStart() {
        return !this.rat.getMainHandStack().isEmpty() && !this.rat.getEntityWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack())).isEmpty();
    }

    @Override
    public void start() {
        if (!this.rat.getMainHandStack().isEmpty() && !this.rat.getEntityWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> !this.rat.world.isClient() && animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack())).isEmpty()) {
            target = this.rat.getEntityWorld().getEntitiesByClass(AnimalEntity.class, this.rat.getBoundingBox().expand(16, 4, 16), animalEntity -> animalEntity.getBreedingAge() == 0 && animalEntity.canEat() && animalEntity.isBreedingItem(this.rat.getMainHandStack())).get(0);
        }
    }

    @Override
    public void tick() {
        ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

        if (this.rat.squaredDistanceTo(target.getX(), target.getY(), target.getZ()) <= 5) {
            if (this.rat.getOwner() instanceof PlayerEntity) {
                itemStack.decrement(1);
                target.lovePlayer((PlayerEntity) this.rat.getOwner());
            }

            target = null;
        } else {
            this.rat.getNavigation().startMovingTo(target, 1D);
        }
    }

    @Override
    public boolean shouldContinue() {
        return target != null && !this.rat.getMainHandStack().isEmpty() && target.getBreedingAge() == 0 && target.canEat() && target.isBreedingItem(this.rat.getMainHandStack());
    }
}
