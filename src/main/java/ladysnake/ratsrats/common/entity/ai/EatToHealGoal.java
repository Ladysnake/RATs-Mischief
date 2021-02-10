package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class EatToHealGoal extends Goal {
    public final RatEntity rat;
    public int eatingTicks;

    public EatToHealGoal(RatEntity rat) {
        this.rat = rat;
    }

    @Override
    public boolean canStart() {
        return this.rat.isBreedingItem(this.rat.getMainHandStack()) && this.rat.getHealth() < this.rat.getMaxHealth() && !this.rat.getMoveControl().isMoving();
    }

    @Override
    public void tick() {
        if (!this.rat.world.isClient() && this.rat.isEating()) {
            if (!this.rat.getMoveControl().isMoving()) {
                super.tick();

                eatingTicks--;

                if (this.eatingTicks % 4 == 0 && this.eatingTicks > 0) {
                    if (this.rat.getMainHandStack().isFood()) {
                        ((ServerWorld) this.rat.world).spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, this.rat.getMainHandStack()), this.rat.getX() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getY() + this.rat.getRandom().nextGaussian() / 20f, this.rat.getZ() + this.rat.getRandom().nextGaussian() / 20f, 5, this.rat.getRandom().nextGaussian() / 20f, 0.2D + this.rat.getRandom().nextGaussian() / 20f, this.rat.getRandom().nextGaussian() / 20f, 0.1f);
                        ((ServerWorld) this.rat.world).playSoundFromEntity(null, this.rat, SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.NEUTRAL, 0.01F, this.rat.getRandom().nextFloat() * 0.4F + 0.8F);
                    }
                }

                if (this.eatingTicks <= 0) {
                    if (this.rat.getMainHandStack().isFood()) {

                        this.rat.heal((float) this.rat.getMainHandStack().getItem().getFoodComponent().getHunger());
                        this.rat.getMainHandStack().getItem().getFoodComponent().getStatusEffects().forEach(statusEffectInstanceFloatPair -> {
                            this.rat.addStatusEffect(statusEffectInstanceFloatPair.getFirst());
                        });
                        this.rat.getMainHandStack().decrement(1);
                        this.rat.setEating(false);
                    }
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
