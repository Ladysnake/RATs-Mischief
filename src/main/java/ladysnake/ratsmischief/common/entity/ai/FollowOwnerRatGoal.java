package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public class FollowOwnerRatGoal extends Goal {
	private final RatEntity rat;
	private final WorldView world;
	private final double speed;
	private final EntityNavigation navigation;
	private final float maxDistance;
	private final float minDistance;
	private final boolean leavesAllowed;
	private LivingEntity owner;
	private int updateCountdownTicks;
	private float oldWaterPathfindingPenalty;

	public FollowOwnerRatGoal(RatEntity rat, double speed, float minDistance, float maxDistance, boolean leavesAllowed) {
		this.rat = rat;
		this.world = rat.getWorld();
		this.speed = speed;
		this.navigation = rat.getNavigation();
		this.minDistance = minDistance;
		this.maxDistance = maxDistance;
		this.leavesAllowed = leavesAllowed;
		this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
		if (!(rat.getNavigation() instanceof MobNavigation) && !(rat.getNavigation() instanceof BirdNavigation)) {
			throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
		}
	}

	@Override
	public boolean canStart() {
		LivingEntity livingEntity = this.rat.getOwner();

		if (livingEntity == null || this.rat.isSpy()) {
			return false;
		} else if (livingEntity.isSpectator()) {
			return false;
		} else if (this.rat.isSitting()) {
			return false;
		} else if (this.rat.squaredDistanceTo(livingEntity) < (double) (this.minDistance * this.minDistance) && !this.rat.canReturnToOwnerInventory()) {
			return false;
		} else {
			this.owner = livingEntity;
			return true;
		}
	}

	@Override
	public boolean shouldContinue() {
		if (this.navigation.isIdle() || this.rat.isSpy()) {
			return false;
		} else if (this.rat.isSitting()) {
			return false;
		} else {
			return this.rat.squaredDistanceTo(this.owner) > (double) (this.maxDistance * this.maxDistance) && !this.rat.canReturnToOwnerInventory();
		}
	}

	@Override
	public void start() {
		this.updateCountdownTicks = 0;
		this.oldWaterPathfindingPenalty = this.rat.getPathfindingPenalty(PathNodeType.WATER);
		this.rat.setPathfindingPenalty(PathNodeType.WATER, 0.0F);
	}

	@Override
	public void stop() {
		this.owner = null;
		this.navigation.stop();
		this.rat.setPathfindingPenalty(PathNodeType.WATER, this.oldWaterPathfindingPenalty);
	}

	@Override
	public void tick() {
		this.rat.getLookControl().lookAt(this.owner, 10.0F, (float) this.rat.getMaxLookPitchChange());
		if (--this.updateCountdownTicks <= 0) {
			this.updateCountdownTicks = 10;
			if (!this.rat.isLeashed() && !this.rat.hasVehicle()) {
				if (this.rat.squaredDistanceTo(this.owner) >= 500D && !this.rat.isFlying()) {
					this.tryTeleport();
				} else {
					this.navigation.startMovingTo(this.owner, this.speed);
				}
			}
		}
	}

	private void tryTeleport() {
		this.rat.fallDistance = 0f;
		BlockPos blockPos = this.owner.getBlockPos();

		for (int i = 0; i < 10; ++i) {
			int j = this.getRandomInt(-3, 3);
			int k = this.getRandomInt(-1, 1);
			int l = this.getRandomInt(-3, 3);
			boolean bl = this.tryTeleportTo(blockPos.getX() + j, blockPos.getY() + k, blockPos.getZ() + l);
			if (bl) {
				return;
			}
		}

	}

	private boolean tryTeleportTo(int x, int y, int z) {
		if (Math.abs((double) x - this.owner.getX()) < 2.0D && Math.abs((double) z - this.owner.getZ()) < 2.0D) {
			return false;
		} else if (!this.canTeleportTo(new BlockPos(x, y, z))) {
			return false;
		} else {
			this.rat.refreshPositionAndAngles((double) x + 0.5D, y, (double) z + 0.5D, this.rat.getYaw(), this.rat.getPitch());
			this.navigation.stop();
			return true;
		}
	}

	private boolean canTeleportTo(BlockPos pos) {
		PathNodeType pathNodeType = LandPathNodeMaker.getLandNodeType(this.world, pos.mutableCopy());
		if (pathNodeType != PathNodeType.WALKABLE) {
			return false;
		} else {
			BlockState blockState = this.world.getBlockState(pos.down());
			if (!this.leavesAllowed && blockState.isIn(BlockTags.LEAVES)) {
				return false;
			} else {
				BlockPos blockPos = pos.subtract(this.rat.getBlockPos());
				return this.world.isSpaceEmpty(this.rat, this.rat.getBoundingBox().offset(blockPos));
			}
		}
	}

	private int getRandomInt(int min, int max) {
		return this.rat.getRandom().nextInt(max - min + 1) + min;
	}
}
