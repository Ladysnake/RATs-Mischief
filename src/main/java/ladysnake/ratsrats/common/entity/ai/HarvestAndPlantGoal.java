package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class HarvestAndPlantGoal extends Goal {
    protected final RatEntity rat;
    protected BlockPos targetBlockPos;

    public HarvestAndPlantGoal(RatEntity rat) {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
        this.rat = rat;
    }

    public boolean canStart() {
        targetBlockPos = null;

        if (this.rat.getTarget() == null && this.rat.getAttacker() == null && !this.rat.isSitting() && this.rat.isTamed() && (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).isEmpty() || (this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) this.rat.getEquippedStack(EquipmentSlot.MAINHAND).getItem()).getBlock() instanceof CropBlock))) {
            ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

            for (BlockPos blockPos : BlockPos.iterateOutwards(this.rat.getBlockPos(), 8, 2, 8)) {
                if (itemStack.isEmpty()) {
                    BlockState blockState = this.rat.world.getBlockState(blockPos);
                    if (blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState)) {
                        if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
                            targetBlockPos = blockPos;
                            return true;
                        }
                    }
                } else if (itemStack.getItem() instanceof AliasedBlockItem && ((AliasedBlockItem) itemStack.getItem()).getBlock() instanceof CropBlock) {
                    BlockState blockState = this.rat.world.getBlockState(blockPos.add(0, -1, 0));
                    if (blockState.getBlock() == Blocks.FARMLAND && this.rat.world.getBlockState(blockPos).isAir()) {
                        if (this.rat.getNavigation().startMovingTo(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 1f)) {
                            targetBlockPos = blockPos;
                            return true;
                        }
                    }
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public void tick() {
        ItemStack itemStack = this.rat.getEquippedStack(EquipmentSlot.MAINHAND);

        if (itemStack.isEmpty()) {
            BlockState blockState = this.rat.world.getBlockState(targetBlockPos);
            if (!(blockState.getBlock() instanceof CropBlock && ((CropBlock) blockState.getBlock()).isMature(blockState))) {
                this.canStart();
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                this.rat.world.breakBlock(targetBlockPos, true, this.rat);
                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
            }
        } else {
            BlockState blockState = this.rat.world.getBlockState(targetBlockPos.add(0, -1, 0));
            if (!(blockState.getBlock() == Blocks.FARMLAND && this.rat.world.getBlockState(targetBlockPos).isAir())) {
                this.canStart();
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                this.rat.world.setBlockState(targetBlockPos, ((AliasedBlockItem) itemStack.getItem()).getBlock().getDefaultState());
                this.rat.getEquippedStack(EquipmentSlot.MAINHAND).decrement(1);
                targetBlockPos = null;
            } else {
                this.rat.getNavigation().startMovingTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 1D);
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
