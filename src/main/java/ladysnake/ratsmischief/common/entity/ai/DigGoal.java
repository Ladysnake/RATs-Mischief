package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import ladysnake.ratsmischief.common.item.RatMasterArmorItem;
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

	@Override
	public boolean canStart() {
		this.targetBlockPos = null;
		this.breakProgress = 0;
		if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty()) {
			ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);
			if (itemStack.isEmpty()) {
				for (BlockPos blockPos : BlockPos.iterateOutwards(this.rat.getBlockPos(), 8, 2, 8)) {
					BlockState blockState = this.rat.getWorld().getBlockState(blockPos);
					if (blockState.getBlock() == this.targetBlock) {
						int strength = 0;
						StatusEffectInstance effect = this.rat.getStatusEffect(StatusEffects.STRENGTH);
						if (effect != null) {
							strength = effect.getAmplifier() + 1;
						}
						if (blockState.getHardness(this.rat.getWorld(), blockPos) >= 0 && blockState.getHardness(this.rat.getWorld(), blockPos) <= 1f + strength) {
							if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
								this.targetBlockPos = blockPos;
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public void tick() {
		ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

		if (itemStack.isEmpty()) {
			BlockState blockState = this.rat.getWorld().getBlockState(this.targetBlockPos);
			if (blockState.getBlock() != this.targetBlock) {
				this.canStart();
				return;
			}

			int haste = 0;
			StatusEffectInstance effect = this.rat.getStatusEffect(StatusEffects.HASTE);
			if (effect != null) {
				haste = effect.getAmplifier() + 1;
			}

			if (this.rat.squaredDistanceTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) <= 5) {
				float progressIncrease = 0.015f + 0.003f * haste;
				progressIncrease *= RatMasterArmorItem.getMiningSpeedMultiplier(this.rat.getOwner());
				this.breakProgress += progressIncrease;
				this.rat.getWorld().setBlockBreakingInfo(this.rat.getId(), this.targetBlockPos, (int) (this.breakProgress / this.rat.getWorld().getBlockState(this.targetBlockPos).getHardness(this.rat.getWorld(), this.targetBlockPos) * 9));
				if (this.breakProgress >= this.rat.getWorld().getBlockState(this.targetBlockPos).getHardness(this.rat.getWorld(), this.targetBlockPos)) {
					this.rat.getWorld().setBlockBreakingInfo(this.rat.getId(), this.targetBlockPos, -1);
					this.rat.getWorld().breakBlock(this.targetBlockPos, true, this.rat);
					this.targetBlockPos = null;
				}
			} else {
				this.rat.getNavigation().startMovingTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 1D);
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return this.targetBlockPos != null && (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty());
	}

	@Override
	public void start() {
		super.start();
	}
}
