package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.PotionItem;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.UseAction;

public class EatToHealGoal extends Goal {
    public final RatEntity rat;
    public int eatingTicks;

    public EatToHealGoal(RatEntity rat) {
        this.rat = rat;
    }

    @Override
    public boolean canStart() {
        return (this.rat.isBreedingItem(this.rat.getMainHandStack()) || this.rat.getMainHandStack().getItem() instanceof PotionItem) && this.rat.getHealth() < this.rat.getMaxHealth() && !this.rat.getMoveControl().isMoving();
    }

    @Override
    public void tick() {
        if (!this.rat.world.isClient() && this.rat.isEating()) {
            if (!this.rat.getMoveControl().isMoving()) {
                super.tick();

                eatingTicks--;

                if (this.eatingTicks % 4 == 0 && this.eatingTicks > 0) {
                    if (this.rat.getMainHandStack().getItem().getUseAction(this.rat.getMainHandStack()) == UseAction.DRINK) {
                        ((ServerWorld) this.rat.world).playSoundFromEntity(null, this.rat, this.rat.getMainHandStack().getItem().getDrinkSound(), SoundCategory.NEUTRAL, 0.01F, this.rat.getRandom().nextFloat() * 0.4F + 0.8F);
                    } else if (this.rat.getMainHandStack().getItem().getUseAction(this.rat.getMainHandStack()) == UseAction.EAT) {
                        ((ServerWorld) this.rat.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.rat.getMainHandStack()), this.rat.getX() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getY() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getZ() + this.rat.getRandom().nextGaussian() / 20f, 5, this.rat.getRandom().nextGaussian() / 20f, 0.2D + this.rat.getRandom().nextGaussian() / 20f, this.rat.getRandom().nextGaussian() / 20f, 0.025f);
                        ((ServerWorld) this.rat.world).playSoundFromEntity(null, this.rat, this.rat.getMainHandStack().getItem().getEatSound(), SoundCategory.NEUTRAL, 0.01F, this.rat.getRandom().nextFloat() * 0.4F + 0.8F);
                    }
                }

                if (this.eatingTicks <= 0) {

                    if (this.rat.getMainHandStack().getItem().isFood()) {
                        this.rat.heal((float) this.rat.getMainHandStack().getItem().getFoodComponent().getHunger());
                        this.rat.getMainHandStack().getItem().getFoodComponent().getStatusEffects().forEach(statusEffectInstanceFloatPair -> {
                            this.rat.addStatusEffect(statusEffectInstanceFloatPair.getFirst());
                        });
                    }
                    this.rat.setEating(false);
                    this.rat.getMainHandStack().getItem().finishUsing(this.rat.getMainHandStack(), this.rat.world, this.rat);

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
