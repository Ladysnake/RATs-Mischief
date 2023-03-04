package doctor4t.ratsmischief.common.entity.ai;

import doctor4t.ratsmischief.common.entity.RatEntity;
import doctor4t.ratsmischief.common.item.RatMasterArmorItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class DigGoal extends Goal {
	protected final RatEntity rat;
	protected BlockPos targetBlockPos;
	protected float breakProgress;
	protected Block targetBlock;

	public DigGoal(RatEntity rat, Block block) {
		this.setControls(EnumSet.of(Control.MOVE));
		this.rat = rat;
		this.targetBlock = block;
	}

	public boolean canStart() {
		targetBlockPos = null;
		breakProgress = 0;
		if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
			ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty()) {
				for (BlockPos blockPos : BlockPos.iterateOutwards(this.rat.getBlockPos(), 8, 2, 8)) {
					BlockState blockState = this.rat.world.getBlockState(blockPos);
					if (blockState.getBlock() == targetBlock) {
						int strength = 0;
						StatusEffectInstance effect = this.rat.getStatusEffect(StatusEffects.STRENGTH);
						if (effect != null) {
							strength = effect.getAmplifier() + 1;
						}
						if (blockState.getHardness(this.rat.world, blockPos) >= 0 && blockState.getHardness(this.rat.world, blockPos) <= 1f + strength) {
							if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
								targetBlockPos = blockPos;
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	public void tick() {
		ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

		if (itemStack.isEmpty()) {
			BlockState blockState = this.rat.world.getBlockState(targetBlockPos);
			if (blockState.getBlock() != targetBlock) {
				this.canStart();
				return;
			}

			int haste = 0;
			StatusEffectInstance effect = this.rat.getStatusEffect(StatusEffects.HASTE);
			if (effect != null) {
				haste = effect.getAmplifier() + 1;
			}

			if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
				float progressIncrease = 0.015f + 0.003f * haste;
				progressIncrease *= RatMasterArmorItem.getMiningSpeedMultiplier(this.rat.getOwner());
				breakProgress += progressIncrease;
				this.rat.world.setBlockBreakingInfo(this.rat.getId(), targetBlockPos, (int) (breakProgress / this.rat.world.getBlockState(targetBlockPos).getHardness(this.rat.world, targetBlockPos) * 9));
				if (breakProgress >= this.rat.world.getBlockState(targetBlockPos).getHardness(this.rat.world, targetBlockPos)) {
					this.rat.world.setBlockBreakingInfo(this.rat.getId(), targetBlockPos, -1);
					this.rat.world.breakBlock(targetBlockPos, true, this.rat);
					targetBlockPos = null;
				}
			} else {
				this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return targetBlockPos != null && (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty());
	}

	@Override
	public void start() {
		super.start();
	}
}
