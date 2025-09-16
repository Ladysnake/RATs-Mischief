package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.function.Predicate;

public class ChaseForFunGoal<T extends LivingEntity> extends TrackTargetGoal {
	protected final Class<T> targetClass;
	protected final RatEntity rat;
	protected LivingEntity targetEntity;
	protected TargetPredicate targetPredicate;

	public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility) {
		this(mob, targetClass, checkVisibility, false);
	}

	public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate) {
		this(mob, targetClass, checkVisibility, checkCanNavigate, null);
	}

	public ChaseForFunGoal(MobEntity mob, Class<T> targetClass, boolean checkVisibility, boolean checkCanNavigate, @Nullable TargetPredicate.EntityPredicate targetPredicate) {
		super(mob, checkVisibility, checkCanNavigate);
		this.targetClass = targetClass;
		this.setControls(EnumSet.of(Control.TARGET));
		this.targetPredicate = (TargetPredicate.createNonAttackable()).setBaseMaxDistance(this.getFollowRange()).setPredicate(targetPredicate);
		this.rat = (RatEntity) mob;
	}

	@Override
	public void tick() {
		this.mob.getNavigation().startMovingTo(this.targetEntity, 1f);
	}

	@Override
	public boolean canStart() {
		if (this.rat.canReturnToOwnerInventory()) {
			return false;
		}

		this.findClosestTarget();
		return this.targetEntity != null && !this.rat.isSitting();
	}

	@Override
	public boolean shouldContinue() {
		if (this.rat.canReturnToOwnerInventory()) {
			return false;
		}

		return this.targetEntity != null && !this.targetEntity.isDead() && this.mob.getRandom().nextInt(10) != 0 && !this.rat.isSitting();
	}

	protected Box getSearchBox(double distance) {
		return this.mob.getBoundingBox().expand(distance, 4.0D, distance);
	}

	protected void findClosestTarget() {
		double d = (double)-1.0F;
		LivingEntity entity = null;

		for(LivingEntity entity1 : this.mob.getWorld().getEntitiesByClass(targetClass, this.getSearchBox(10), e -> true)) {
			if (targetPredicate == null || targetPredicate.test((ServerWorld) this.mob.getWorld(), this.mob, entity1)) {
				double e = entity1.squaredDistanceTo(this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
				if (d == (double)-1.0F || e < d) {
					d = e;
					entity = entity1;
				}
			}
		}
		this.targetEntity = entity;
	}

	public void setTargetEntity(@Nullable LivingEntity targetEntity) {
		this.targetEntity = targetEntity;
	}
}
