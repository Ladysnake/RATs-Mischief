package ladysnake.ratsmischief.common.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ChaseForFunGoal<T extends LivingEntity> extends TrackTargetGoal {
    protected final Class<T> targetClass;
    protected LivingEntity targetEntity;
    protected TargetPredicate targetPredicate;
    protected final TameableEntity tameable;

    public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
        this(mob, targetClass, checkVisibility, false);
    }

    public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
        this(mob, targetClass, checkVisibility, checkCanNavigate, null);
    }

    public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate, @Nullable Predicate<LivingEntity> targetPredicate) {
        super(mob, checkVisibility, checkCanNavigate);
        this.targetClass = targetClass;
        this.setControls(EnumSet.of(Goal.Control.TARGET));
        this.targetPredicate = (new TargetPredicate()).setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
        this.tameable = (TameableEntity) mob;
    }

    @Override
    public void tick() {
        this.mob.getNavigation().startMovingTo(this.targetEntity, 1f);
    }

    public boolean canStart() {
        this.findClosestTarget();
        return this.targetEntity != null && !this.tameable.isSitting();
    }

    @Override
    public boolean shouldContinue() {
        return this.targetEntity != null && !this.targetEntity.isDead() && this.mob.getRandom().nextInt(10) != 0 && !this.tameable.isSitting();
    }

    protected Box getSearchBox(double distance) {
        return this.mob.getBoundingBox().expand(distance, 4.0D, distance);
    }

    protected void findClosestTarget() {
        this.targetEntity = this.mob.world.getClosestEntityIncludingUngeneratedChunks(this.targetClass, this.targetPredicate, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ(), this.getSearchBox(10));
    }

    public void setTargetEntity(@Nullable LivingEntity targetEntity) {
        this.targetEntity = targetEntity;
    }
}
