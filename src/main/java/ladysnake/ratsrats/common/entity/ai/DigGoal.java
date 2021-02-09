package ladysnake.ratsrats.common.entity.ai;

import ladysnake.ratsrats.common.entity.RatEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.EnumSet;

public class DigGoal extends Goal {
    protected final RatEntity rat;
    protected BlockPos targetBlockPos;
    protected float breakProgress;
    protected Block targetBlock;

    public DigGoal(RatEntity rat, Block block) {
        this.setControls(EnumSet.of(Goal.Control.MOVE));
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
            if (blockState.getBlock() != targetBlock) {
                this.canStart();
                return;
            }

            if (this.rat.squaredDistanceTo(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ()) <= 5) {
                breakProgress += Math.max(0, 1 -this.rat.world.getBlockState(targetBlockPos).getBlock().getBlastResistance());
                this.rat.world.setBlockBreakingInfo(this.rat.getEntityId(), targetBlockPos, (int) breakProgress);
                if (breakProgress >= 9 || this.rat.world.getBlockState(targetBlockPos).getBlock().getBlastResistance() == 0.0f) {
                    this.rat.world.setBlockBreakingInfo(this.rat.getEntityId(), targetBlockPos, -1);
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
