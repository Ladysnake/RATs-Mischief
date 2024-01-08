package ladysnake.ratsmischief.common.entity.ai;

import ladysnake.ratsmischief.common.entity.RatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class HarvestPlantMealGoal extends Goal {
	protected final RatEntity rat;
	protected BlockPos targetBlockPos;

	public HarvestPlantMealGoal(RatEntity rat) {
		this.setControls(EnumSet.of(Control.MOVE));
		this.rat = rat;
	}

	@Override
	public boolean canStart() {
		this.targetBlockPos = null;

		if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem()).getBlock() instanceof CropBlock) || this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof BoneMealItem)) {
			ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

			for (BlockPos blockPos : BlockPos.iterateOutwards(this.rat.getBlockPos(), 8, 2, 8)) {
				if (itemStack.isEmpty()) {
					// harvest
					BlockState blockState = this.rat.getWorld().getBlockState(blockPos);
					if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState)) {
						if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
							this.targetBlockPos = blockPos;
							return true;
						}
					}
				} else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
					// plant
					BlockState blockState = this.rat.getWorld().getBlockState(blockPos.add(0, -1, 0));
					if (blockState.getBlock() instanceof FarmlandBlock && this.rat.getWorld().getBlockState(blockPos).isAir()) {
						if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
							this.targetBlockPos = blockPos;
							return true;
						}
					}
				} else if (itemStack.getItem() instanceof BoneMealItem) {
					// bonemeal
					BlockState blockState = this.rat.getWorld().getBlockState(blockPos);
					if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isFertilizable(this.rat.getWorld(), blockPos, this.rat.getWorld().getBlockState(blockPos), this.rat.getWorld().isClient())) {
						if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
							this.targetBlockPos = blockPos;
							return true;
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
			// harvest
			BlockState blockState = this.rat.getWorld().getBlockState(this.targetBlockPos);
			if (!(blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState))) {
				this.canStart();
			}

			if (this.rat.squaredDistanceTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) <= 5) {
				this.rat.getWorld().breakBlock(this.targetBlockPos, true, this.rat);
				this.targetBlockPos = null;
			} else {
				this.rat.getNavigation().startMovingTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 1D);
			}
		} else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
			// plant
			BlockState blockState = this.rat.getWorld().getBlockState(this.targetBlockPos.add(0, -1, 0));
			if (!(blockState.getBlock() instanceof FarmlandBlock && this.rat.getWorld().getBlockState(this.targetBlockPos).isAir())) {
				this.canStart();
			}

			if (this.rat.squaredDistanceTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) <= 5) {
				this.rat.getWorld().setBlockState(this.targetBlockPos, ((AliasedBlockItem) itemStack.getItem()).getBlock().getDefaultState());
				this.rat.getEquippedStack(EquipmentSlot.MAINHAND).decrement(1);
				this.targetBlockPos = null;
			} else {
				this.rat.getNavigation().startMovingTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 1D);
			}
		} else if (itemStack.getItem() instanceof BoneMealItem) {
			// bonemeal
			BlockState blockState = this.rat.getWorld().getBlockState(this.targetBlockPos);
			if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isFertilizable(this.rat.getWorld(), this.targetBlockPos, this.rat.getWorld().getBlockState(this.targetBlockPos), this.rat.getWorld().isClient())) {
				this.canStart();
			}

			if (this.rat.squaredDistanceTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ()) <= 5) {
				BoneMealItem.useOnFertilizable(itemStack, this.rat.getWorld(), this.targetBlockPos);

				ServerWorld serverWorld = (ServerWorld) this.rat.getWorld();

				serverWorld.playSound(null, this.targetBlockPos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.NEUTRAL, 1.0f, 1.0f);

				serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER, this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 15, 0.5f, 0.2f, 0.5f, 0);

				this.targetBlockPos = null;
			} else {
				this.rat.getNavigation().startMovingTo(this.targetBlockPos.getX(), this.targetBlockPos.getY(), this.targetBlockPos.getZ(), 1D);
			}
		}
	}

	@Override
	public boolean shouldContinue() {
		return super.shouldContinue();
	}

	@Override
	public void start() {
		super.start();
	}
}
