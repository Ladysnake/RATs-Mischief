package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class RatMeleeAttackGoal extends Goal {
	private static final long MAX_ATTACK_TIME = 20L;
	protected final RatEntity rat;
	private final double speed;
	private final boolean pauseWhenMobIdle;
	private final int attackIntervalTicks = 20;
	private Path path;
	private double targetX;
	private double targetY;
	private double targetZ;
	private int updateCountdownTicks;
	private int cooldown;
	private long lastUpdateTime;

	public RatMeleeAttackGoal(RatEntity rat, double speed, boolean pauseWhenMobIdle) {
		this.rat = rat;
		this.speed = speed;
		this.pauseWhenMobIdle = pauseWhenMobIdle;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
	}

	@Override
	public boolean canStart() {
		if (this.rat.canReturnToOwnerInventory()) {
			return false;
		}

		long l = this.rat.getWorld().getTime();
		if (l - this.lastUpdateTime < 20L) {
			return false;
		} else {
			this.lastUpdateTime = l;
			LivingEntity livingEntity = this.rat.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else {
				this.path = this.rat.getNavigation().findPathTo(livingEntity, 0);
				if (this.path != null) {
					return true;
				} else {
					return this.getSquaredMaxAttackDistance(livingEntity) >= this.rat.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
				}
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.rat.canReturnToOwnerInventory()) {
			return false;
		}

		LivingEntity livingEntity = this.rat.getTarget();
		if (livingEntity == null) {
			return false;
		} else if (!livingEntity.isAlive()) {
			return false;
		} else if (!this.pauseWhenMobIdle) {
			return !this.rat.getNavigation().isIdle();
		} else if (!this.rat.isInWalkTargetRange(livingEntity.getBlockPos())) {
			return false;
		} else {
			return !(livingEntity instanceof PlayerEntity player) || !livingEntity.isSpectator() && !player.isCreative();
		}
	}

	@Override
	public void start() {
		this.rat.getNavigation().startMovingAlong(this.path, this.speed);
		this.rat.setAttacking(true);
		this.updateCountdownTicks = 0;
		this.cooldown = 0;
	}

	@Override
	public void stop() {
		LivingEntity livingEntity = this.rat.getTarget();
		if (!EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(livingEntity)) {
			this.rat.setTarget(null);
		}

		this.rat.setAttacking(false);
		this.rat.getNavigation().stop();
	}

	@Override
	public boolean shouldRunEveryTick() {
		return true;
	}

	@Override
	public void tick() {
		LivingEntity livingEntity = this.rat.getTarget();
		if (livingEntity != null) {
			this.rat.getLookControl().lookAt(livingEntity, 30.0F, 30.0F);
			double d = this.rat.squaredDistanceTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ());
			this.updateCountdownTicks = Math.max(this.updateCountdownTicks - 1, 0);
			if ((this.pauseWhenMobIdle || this.rat.getVisibilityCache().canSee(livingEntity))
				&& this.updateCountdownTicks <= 0
				&& (
				this.targetX == 0.0 && this.targetY == 0.0 && this.targetZ == 0.0
					|| livingEntity.squaredDistanceTo(this.targetX, this.targetY, this.targetZ) >= 1.0
					|| this.rat.getRandom().nextFloat() < 0.05F
			)) {
				this.targetX = livingEntity.getX();
				this.targetY = livingEntity.getY();
				this.targetZ = livingEntity.getZ();
				this.updateCountdownTicks = 4 + this.rat.getRandom().nextInt(7);
				if (d > 1024.0) {
					this.updateCountdownTicks += 10;
				} else if (d > 256.0) {
					this.updateCountdownTicks += 5;
				}

				if (!this.rat.getNavigation().startMovingTo(livingEntity, this.speed)) {
					this.updateCountdownTicks += 15;
				}

				this.updateCountdownTicks = this.getTickCount(this.updateCountdownTicks);
			}

			this.cooldown = Math.max(this.cooldown - 1, 0);
			this.attack(livingEntity, d);
		}
	}

	protected void attack(LivingEntity target, double squaredDistance) {
		double d = this.getSquaredMaxAttackDistance(target);
		if ((squaredDistance <= d || target.getFirstPassenger() == this.rat) && this.cooldown <= 0) {
			this.resetCooldown();
			this.rat.swingHand(Hand.MAIN_HAND);
			this.rat.tryAttack(target);
		}

	}

	protected void resetCooldown() {
		this.cooldown = this.getTickCount(20);
	}

	protected boolean isCooledDown() {
		return this.cooldown <= 0;
	}

	protected int getCooldown() {
		return this.cooldown;
	}

	protected int getMaxCooldown() {
		return this.getTickCount(20);
	}

	protected double getSquaredMaxAttackDistance(LivingEntity entity) {
		return this.rat.getWidth() * 2.0F * this.rat.getWidth() * 2.0F + entity.getWidth();
	}
}
